/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;

/**
 *
 * @author pcgejza
 */
public class Egraph {
    
    private HashMap<String, Eedge> edges;
    private HashMap<String, Enode> nodes;
    
    public Egraph(){
	edges = new HashMap<String, Eedge>();
	nodes = new HashMap<String, Enode>();
    }
    
    public void displayGraph(){
        
        Graph graph = new SingleGraph("Disp 1");
        graph.addAttribute("ui.stylesheet", styleSheet);
        Iterator it = null;
        Enode n = null, n1 = null, n2 = null;
        Eedge e = null;
        
        
        it = this.nodes.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            n = (Enode)  pair.getValue();
            graph.addNode(n.getName()).addAttribute("ui.label", n.getName());
        }
        
        it = this.edges.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            e = (Eedge)  pair.getValue();
            n1 =  e.getNode1();
            n2 =  e.getNode2();
            graph.addEdge(e.getId(), n1.getName(), n2.getName()).addAttribute("ui.label", e.getFlow()+"/"+e.getCapacity());
        }
        
        graph.display();
        
    }

    
    public void addNode(String id){
        this.nodes.put(id, new Enode(id));
    }
    
    public Enode getNode(String key){
        return this.nodes.get(key);
    }
    
    public void addEdge(String node1, String node2, double capacity, double flow){
        Enode enode1 = this.nodes.get(node1);
        Enode enode2 = this.nodes.get(node2);
        
        this.edges.put(node1+"_"+node2,new Eedge(enode1, enode2, capacity, flow));
        
        Eedge e = this.edges.get(node1+"_"+node2);
        enode1.addOutEdge(node1+"_"+node2, e);
        enode2.addInEdge(node1+"_"+node2, e);
    }
    
    
    public void printEdges(){
        Iterator it = this.edges.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            Eedge ee = (Eedge)  pair.getValue();
            System.out.println(pair.getKey() + " = " +ee.getCapacity());
            //it.remove(); // avoids a ConcurrentModificationException
        }
    }
    
    /**
     * DEFINITION 1
     * A gráf commodity-e s és t csúcs között
     * @param s
     * @param t
     * @return 
     */
    public boolean isCommodity(String s, String t){
        Enode sNode = this.nodes.get(s);
        Enode tNode = this.nodes.get(t);
        Enode v = null;
        Eedge e = null;
        double vOutSum = 0.0 , vInSum = 0.0;
        int vCountEx = 0;
        int vCountInEx = 0;
        Iterator it = null;
        
        // 1. feltétel ∀v ∈ V \ {s, t}: ∑ e∈out(v) F(e) = ∑ e∈in(v) F(e)
        it = this.nodes.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            v = (Enode) pair.getValue();
            
            if(v != sNode && v != tNode){
                vOutSum = v.getAllOutEdgeFlow();
                vInSum = v.getAllInEdgeFlow();
                       
                if(vOutSum == vInSum){
                    vCountEx++;
                }else{
                    vCountInEx++;
                }
                //System.out.println("*** "+v.getName()+" "+vOutSum+" = "+vInSum);
            }
        }
                
        if(vCountInEx == 0){ // amennyiben az első feltétel nem teljesül, false-al térünk vissza
            return false;
        }
        
        // 2. feltétel 
        double sAllOutEdgesFlow = sNode.getAllOutEdgeFlow();
        double tAllInEdgesFlow = sNode.getAllOutEdgeFlow();
        
        
        if(sAllOutEdgesFlow != tAllInEdgesFlow){
            return false;
        }
        
        
        // 3.feltétel
        boolean cfValEth = false;
        it = this.edges.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            e = (Eedge) pair.getValue();
            
            if(e.getFlow() > e.getCapacity()){
                cfValEth = true;
            }
            //System.out.println("E = "+e.getId()+", F : "+e.getFlow()+", c : "+e.getCapacity());
        }
        
        return !cfValEth;
    }
    
    /**
     * DEFINITION 2
     * A gráf single commodity-e s és t csúcs között
     * @param s
     * @param t
     * @return 
     */
    public boolean isSingleCommodity(String s, String t){
        Enode sNode = this.nodes.get(s);
        Enode tNode = this.nodes.get(t);
      
        
        return true;
    }
    
    protected String styleSheet =
            "node {" +
            "	text-size: 25px;" +
            "	size: 15px;" +
            "}" +
            "node.marked {" +
            "	fill-color: red;" +
            "}";
}