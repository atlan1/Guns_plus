package team.GunsPlus.Block;


import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.block.design.GenericBlockDesign;
import org.getspout.spoutapi.block.design.Quad;
import org.getspout.spoutapi.block.design.SubTexture;
import org.getspout.spoutapi.block.design.Texture;

import team.GunsPlus.GunsPlus;


public class TripodDesign extends GenericBlockDesign {

	
	public TripodDesign(GunsPlus plugin, String texture){
			SpoutManager.getFileManager().addToCache(plugin, texture);
			this.setBoundingBox(0, 0, 0, 1, 1, 1);
			this.setRenderPass(0);
			Texture tex = new Texture(plugin, texture, 16, 16, 1);
			this.setTexture(plugin, tex);
			this.setQuadNumber(8);
			SubTexture  subTex = new SubTexture(tex, 0, 0, 1);
			
			Quad edge1 = new Quad(0, subTex);
			edge1.addVertex(0, 0.5f, 1.0f, 0.5f);
			edge1.addVertex(1, 0.5f, 0.9f, 0.5f);
			edge1.addVertex(2, 0, 0, 0);
			edge1.addVertex(3, 0.0f, 0.1f, 0.0f);
			
			Quad edge2 = new Quad(1, subTex);
			edge2.addVertex(0, 0.5f, 1.0f, 0.5f);
			edge2.addVertex(1, 0.5f, 0.9f, 0.5f);
			edge2.addVertex(2, 1, 0, 0);
			edge2.addVertex(3, 1.0f, 0.1f, 0.0f);
			
			Quad edge3 = new Quad(2, subTex);
			edge3.addVertex(0, 0.5f, 1.0f, 0.5f);
			edge3.addVertex(1, 0.5f, 0.9f, 0.5f);
			edge3.addVertex(2, 1, 0, 1);
			edge3.addVertex(3, 1.0f, 0.1f, 1.0f);
			
			Quad edge4 = new Quad(3, subTex);
			edge4.addVertex(0, 0.5f, 1.0f, 0.5f);
			edge4.addVertex(1, 0.5f, 0.9f, 0.5f);
			edge4.addVertex(2, 0, 0, 1);
			edge4.addVertex(3, 0.0f, 0.1f, 1.0f);
			
			Quad edge5 = new Quad(4, subTex);
			edge5.addVertex(0, 0.0f, 0.1f, 0.0f);
			edge5.addVertex(1, 0, 0, 0);
			edge5.addVertex(2, 0.5f, 0.9f, 0.5f);
			edge5.addVertex(3, 0.5f, 1.0f, 0.5f);
			
			Quad edge6 = new Quad(5, subTex);
			edge6.addVertex(0, 1.0f, 0.1f, 0.0f);
			edge6.addVertex(1, 1, 0, 0);
			edge6.addVertex(2, 0.5f, 0.9f, 0.5f);
			edge6.addVertex(3, 0.5f, 1.0f, 0.5f);
			
			Quad edge7 = new Quad(6, subTex);
			edge7.addVertex(0, 1.0f, 0.1f, 1.0f);
			edge7.addVertex(1, 1, 0, 1);
			edge7.addVertex(2, 0.5f, 0.9f, 0.5f);
			edge7.addVertex(3, 0.5f, 1.0f, 0.5f);
			
			Quad edge8 = new Quad(7, subTex);
			edge8.addVertex(0, 0.0f, 0.1f, 1.0f);
			edge8.addVertex(1, 0, 0, 1);
			edge8.addVertex(2, 0.5f, 0.9f, 0.5f);
			edge8.addVertex(3, 0.5f, 1.0f, 0.5f);
			
			this.setQuad(edge1);
			this.setQuad(edge2);
			this.setQuad(edge3);
			this.setQuad(edge4);
			this.setQuad(edge5);
			this.setQuad(edge6);
			this.setQuad(edge7);
			this.setQuad(edge8);
	}
	
}
