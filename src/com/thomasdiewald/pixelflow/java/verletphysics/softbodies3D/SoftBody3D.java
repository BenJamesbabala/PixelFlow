package com.thomasdiewald.pixelflow.java.verletphysics.softbodies3D;


import com.thomasdiewald.pixelflow.java.verletphysics.SpringConstraint3D;
import com.thomasdiewald.pixelflow.java.verletphysics.VerletParticle3D;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PShape;

public abstract class SoftBody3D{
  
  
  // for customizing the particle we just extends the original class and
  // Override what we want to customize
  public class CustomVerletParticle3D extends VerletParticle3D{
    
    
    public CustomVerletParticle3D(int idx, float x, float y, float z, float rad) {
      super(idx, x, y, z, rad);
    }
    
    @Override
    public void updateShapeColor(){
      if(use_particles_color){
        setColor(particle_color);
      } else {
        setColor(particle_gray);
      }
//      super.updateShapeColor();
    }
    
  }

  
  // general attributes
  
  // can be used for sub-classes
  public boolean CREATE_STRUCT_SPRINGS = true;
  public boolean CREATE_SHEAR_SPRINGS  = true;
  public boolean CREATE_BEND_SPRINGS   = true;
  
  public boolean self_collisions = false; // true, all particles get a different collision_group_id
  public int collision_group_id;       // particles that share the same id, are ignored during collision tests
  public int num_nodes;                // number of particles for this object
  public int nodes_offset;             // offset in the global array, used for creating a unique id
  public VerletParticle3D[] particles; // particles of this body
  public PShape shp_particles;         // shape for drawing all particles of this body
  
  
  public VerletParticle3D.Param   param_particle = new VerletParticle3D.Param();
  public SpringConstraint3D.Param param_spring   = new SpringConstraint3D.Param();
  
  
  public SoftBody3D(){
  }
  
  
  
  

  
  public void setParam(VerletParticle3D.Param param_particle){
    this.param_particle = param_particle;
  }
  public void setParam(SpringConstraint3D.Param param_spring){
    this.param_spring = param_spring;
  }
  
  


  //////////////////////////////////////////////////////////////////////////////
  // RENDERING
  //////////////////////////////////////////////////////////////////////////////
  public int particle_color = 0x5C000000; // color(0, 92), default
  public int particle_gray  = 0x5C000000; // color(0, 92)
  public boolean use_particles_color = true;
  public float collision_radius_scale = 1.33333f;
  
  public void setParticleColor(int particle_color){
    this.particle_color = particle_color;
  }
  
  public void createParticlesShape(PApplet papplet){
    papplet.shapeMode(PConstants.CORNER);
    shp_particles = papplet.createShape(PShape.GROUP);
    for(int i = 0; i < particles.length; i++){
      float rad = particles[i].rad;
      PShape shp_pa = papplet.createShape(PConstants.POINT, 0, 0);
      shp_pa.setStroke(true);
      shp_pa.setStrokeWeight(5);
      
//      shp_pa.setFill(particle_color);
      
      particles[i].setShape(shp_pa);
      shp_particles.addChild(shp_pa);
    }
//    shp_particles.getTessellation();
  }

  
  public void drawParticles(PGraphics pg){
    if(shp_particles != null){
      pg.shape(shp_particles);
    }
  }
  
  
  public void drawSprings(PGraphics pg, SpringConstraint3D.TYPE type, int display_mode){
    if(display_mode == -1) return;
    if(type == null) return;
    
    float r,g,b,strokeweight;
    float force, force_curr, force_relx;

    pg.beginShape(PConstants.LINES);
    for(int i = 0; i < particles.length; i++){
      VerletParticle3D pa = particles[i];
      for(int j = 0; j < pa.spring_count; j++){
        SpringConstraint3D spring = pa.springs[j];
        VerletParticle3D pb = spring.pb;
        if(!spring.is_the_good_one) continue;
        if(type != spring.type) continue;
              
        switch(spring.type){
          case STRUCT:  strokeweight = 1.00f; r =   0; g =   0; b =   0; break;
          case SHEAR:   strokeweight = 0.80f; r =  70; g = 140; b = 255; break;
          case BEND:    strokeweight = 0.60f; r = 255; g =  90; b =  30; break;
          default: continue;
        }
        
        if(display_mode == 1){
          force_curr = spring.computeForce(); // the force, at this moment
          force_relx = spring.force;          // the force, remaining after the last relaxation step
          force = Math.abs(force_curr) + Math.abs(force_relx);
          r = force * 10000;
          g = force * 1000;
          b = 0;
          
//          force_curr *= 10000f;
//          r = force_curr;
//          g = 0;
//          b = -force_curr;
        } 
      
        pg.strokeWeight(strokeweight);
        pg.stroke(r,g,b);
        pg.vertex(pa.cx, pa.cy, pa.cz); 
        pg.vertex(pb.cx, pb.cy, pb.cz);
      }
    }
    pg.endShape();
    
  }

  


}
  
  
  
 
  