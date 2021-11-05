package com.company;

import org.apache.lucene.util.RamUsageEstimator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


public class VTokenGramDictionary {
	private class Node{
		private int frequency;
		private ArrayList<Node> childrenlist;
		private boolean isLeaf;
		private String data;
		private ArrayList<com.company.IndexInfo> indexlist;
		public Node(){
			frequency=0;
			isLeaf=false;
			data="";
			childrenlist = new ArrayList<>();
			indexlist = new ArrayList<>();
		}
	}

	private Node root;
	private int qmin,qmax;

	public VTokenGramDictionary(int qmin, int qmax){
		root=new Node();
		this.qmin=qmin;
		this.qmax=qmax;
	}
	//找到叶子节点
	public Node findleafNode(Node nownode){
		for (int i = 0; i<nownode.childrenlist.size();i++){
			nownode = nownode.childrenlist.get(i);
			if (nownode.indexlist.size()!=0&&nownode.isLeaf){
				return nownode;
			}else{
				findleafNode(nownode);
			}
		}
		return nownode;
	}
	public void Prune(Node root,int T)
	{
		if(!judgeWhetherHasLeaf(root))//不是叶子节点
		{
			for(int i=0;i<root.childrenlist.size();i++)
			{
				if(root.childrenlist.get(i)!=null)
					Prune(root.childrenlist.get(i),T);
			}
			return;
		}
		if(root.frequency<=T)
		{
			while(root.childrenlist.size()!=0)
			{
				int i = 0;
				Node nodemin = findleafNode(root.childrenlist.get(i));
				if (root.childrenlist.get(i).indexlist.size()!=0){
					//合并倒排列表
					for (int j=0;j<nodemin.indexlist.size();j++){
						root.indexlist.add(nodemin.indexlist.get(j));
						Collections.sort(root.indexlist);
					}
				}
//				//合并倒排列表
//				for (int j=0;j<root.childrenlist.get(i).indexlist.size();j++){
//					if (!root.indexlist.contains(root.childrenlist.get(i).indexlist.get(j))){
//						root.indexlist.add(root.childrenlist.get(i).indexlist.get(j));
//					}
//				}
				if(nodemin!=null)
					root.childrenlist.remove(i);
			}
		}
		else{
			while(root.childrenlist.size()!=0)
			{
				int i = 0;
				if(root.childrenlist.get(i)!=null)
				{
					Node nodemax = findleafNode(root.childrenlist.get(i));
					if (root.childrenlist.get(i).indexlist.size()!=0) {
						//合并倒排列表
						for (int j = 0; j < nodemax.indexlist.size(); j++) {
							root.indexlist.add(nodemax.indexlist.get(j));
							Collections.sort(root.indexlist);
						}
					}
					root.frequency =nodemax.frequency;
					if(root.frequency<=T)
						break;
				}
			}
			for(int i=0;i< root.childrenlist.size();i++)
			{
				if(root.childrenlist.get(i)!=null)
					Prune(root.childrenlist.get(i),T);
			}
		}
	}
	
	public boolean judgeWhetherHasLeaf(Node root)
	{
		if(root.isLeaf)
			return true;
		return false;
	}

	public void printDic(Node root)
	{
		System.out.println("token-gram 输出：");
		Main.count += RamUsageEstimator.sizeOfObject(root);
		for(int i=0;i< root.childrenlist.size();i++)
		{
			if(root.childrenlist.get(i)!=null)
			{
				System.out.println("{"+root.childrenlist.get(i).data+"}     "+root.childrenlist.get(i).frequency);
				if(root.childrenlist.get(i).isLeaf&&!root.childrenlist.get(i).indexlist.isEmpty()){
					for (int j=0;j<root.childrenlist.get(i).indexlist.size();j++){
						System.out.println("indexlist:"+root.childrenlist.get(i).indexlist.get(j));
					}
				}
				printDic(root.childrenlist.get(i));
			}
		}
	}
	

	public void createDic(Node root,ArrayList strs)
	{
		for(int i=0;i<strs.size();i++)
		{
			String s=strs.get(i).toString();
			s=s.toLowerCase();
			String[] tokenArray = s.split(" ");
			int id = i+1;
			for(int j=0;j<tokenArray.length-qmax;j++)
			{
				String[] strings = Arrays.copyOfRange(tokenArray, j, j + qmax);
				insert(root,strings,id,j);
			}
			for(int k=tokenArray.length-qmax;k<tokenArray.length-qmin+1;k++)
			{
				String[] strings = Arrays.copyOfRange(tokenArray, k, tokenArray.length);
				insert(root,strings,id,k);
			}
		}
	}
	public void insert(Node root,String[] tokenArray,int id,int positionStart) {
		for(int i=0;i<tokenArray.length;i++) {
			String gramstr = tokenArray[i];
			boolean containFlag = false;
			int tokenindex = 0;
			for (int k =0;k<root.childrenlist.size();k++){
				String data = root.childrenlist.get(k).data;
				if (data.equals(gramstr)){
					containFlag = true;
					tokenindex = k;
				}
			}
			if (root.childrenlist!=null&&containFlag){
				root.childrenlist.get(tokenindex).frequency ++;
				root.childrenlist.get(tokenindex).data = gramstr.toString();
			}else{
				Node node = new Node();
				node.frequency++;
				node.data = gramstr;
				root.childrenlist.add(node);
				tokenindex = root.childrenlist.size()-1;
			}
			if (i==tokenArray.length-1){
				Node node = root.childrenlist.get(tokenindex);
				node.isLeaf=true;

				com.company.IndexInfo indexInfo = new com.company.IndexInfo();
				int freq = indexInfo.getFreq();
				freq++;
				indexInfo.setFreq(freq);
				indexInfo.setId(id);
				indexInfo.setPosition(positionStart+i);
				node.indexlist.add(indexInfo);
				root.childrenlist.set(tokenindex,node);
			}
			if(i>=qmin){
				root.isLeaf=true;
			}
			root=root.childrenlist.get(tokenindex);
		}
	}

//	public boolean search(Node root,String s)
//	{
//		char[]chrs=s.toLowerCase().toCharArray();
//		for(int i=0;i<chrs.length;i++)
//		{
//			int index=chrs[i]-'a';
//			if(root.children[index]==null)
//			{
//				return false;
//			}
//			if(i==chrs.length-1&&root.children[index].isLeaf==false)
//				return false;
//			root=root.children[index];
//		}
//		return true;
//	}
	
	public Node getroot()
	{
		return root;
	}
	
}

