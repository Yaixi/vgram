package com.company;

import org.apache.lucene.util.RamUsageEstimator;

import java.util.ArrayList;
import java.util.Collections;

public class VGramDictionary {
	private class Node{
		private int frequency;
		private Node children[];
		private boolean isLeaf;
		private String data;
		private ArrayList<IndexInfo> indexlist;
		public Node(){
			frequency=0;
			isLeaf=false;
			data="";
//			children=new Node[26];
			children=new Node[128];
			indexlist = new ArrayList<>();
		}
	}
	
	private Node root;
	private int qmin,qmax;
	
	public VGramDictionary(int qmin,int qmax){
		root=new Node();
		this.qmin=qmin;
		this.qmax=qmax;
	}
	//找到叶子节点
	public Node findleafNode(Node nownode){
		if (nownode.children!=null){
			for (int i = 0; i<nownode.children.length;i++){
				if (nownode.children[i]!=null){
					nownode = nownode.children[i];
					if (nownode!=null&&nownode.indexlist.isEmpty()&&nownode.indexlist.size()!=0&&nownode.isLeaf){
						return nownode;
					}else{
						findleafNode(nownode);
					}
				}

			}
		}

		return nownode;
	}
	public void Prune(Node root,int T)
	{
		if(!judgeWhetherHasLeaf(root))//不是叶子节点
		{
			for(int i=0;i<128;i++)
			{
				if(root.children[i]!=null)
					Prune(root.children[i],T);
			}
			return;
		}
		if(root.frequency<=T)
		{
			for(int i=0;i<128;i++)
			{
				if(root.children[i]!=null){
					//合并倒排列表
					if(root.children[i].indexlist!=null&&root.children[i].indexlist.size()!=0){
						Node node = findleafNode(root.children[i]);
						for (int j=0;j< node.indexlist.size();j++){
//							if (!root.indexlist.contains(root.children[i].indexlist.get(j))){
								root.indexlist.add(node.indexlist.get(j));
								Collections.sort(root.indexlist);
//							}
						}
					}
					root.frequency = root.children[i].frequency;
					root.children[i]=null;
				}
			}
		}
		else{
			for(int i=0;i<128;i++)
			{
				if(root.children[i]!=null)
				{
					root.frequency = root.children[i].frequency;
					//合并倒排列表
					if(root.children[i].indexlist!=null&&root.children[i].indexlist.size()!=0){
						Node node = findleafNode(root.children[i]);
						for (int j=0;j< node.indexlist.size();j++){
//							if (!root.indexlist.contains(root.children[i].indexlist.get(j))){
							root.indexlist.add(node.indexlist.get(j));
							Collections.sort(root.indexlist);
//							}
						}
					}
					root.children[i]=null;
					if(root.frequency<=T)
						break;
				}
			}
			for(int i=0;i<128;i++)
			{
				if(root.children[i]!=null)
					Prune(root.children[i],T);
			}
		}
	}
	
	public boolean judgeWhetherHasLeaf(Node root)
	{
		if(root.isLeaf)
			return true;
		return false;
	}
//	//建立倒排索引列表
//	public void indexlist(Node root){
//		if (judgeWhetherHasLeaf(root)){
//			if (root.isLeaf)
//		}
//	}


	public void printDic(Node root)
	{
		System.out.println("v-gram 输出：");
		Main.count += RamUsageEstimator.sizeOfObject(root);
		for(int i=0;i<128;i++)
		{
			if(root.children[i]!=null)
			{
				System.out.println("{"+root.children[i].data+"}      "+root.children[i].frequency);
				if(root.children[i].isLeaf&&!root.children[i].indexlist.isEmpty()){
					System.out.println("indexlist:"+root.children[i].indexlist);
				}
				printDic(root.children[i]);
			}
		}
	}
	public void insert(Node root,String s,int id,int positionStart)
	{
		int count=0;
		s=s.toLowerCase();
		char []chrs=s.toCharArray();
		for(int i=0;i<chrs.length;i++)
		{
			int index=chrs[i]-' ';
			if(root.children[index]!=null)
			{
				root.children[index].frequency++;
				root.children[index].data=s.substring(0, count+1);
			}
			else
			{
				root.children[index]=new Node();
				root.children[index].frequency++;
				root.children[index].data=s.substring(0, count+1);
			}
			if(i==chrs.length-1){
				root.children[index].isLeaf=true;
				IndexInfo indexInfo = new IndexInfo();
				int freq = indexInfo.getFreq();
				freq++;
				indexInfo.setFreq(freq);
				indexInfo.setId(id);
				indexInfo.setPosition(positionStart+i);
				root.children[index].indexlist.add(indexInfo);
			}
			count+=1;
			if(count>qmin)
//				root.children[index].isLeaf=true;
				root.isLeaf = true;
			root=root.children[index];
		}
	}
	
	public void createDic(Node root,ArrayList strs)
	{
		for(int i=0;i<strs.size();i++)
		{
			String s=strs.get(i).toString();
//			String[] splitstr = s.split(":");
//			String idstr = splitstr[0];
//			int id = Integer.parseInt(idstr);
//			s = splitstr[1];
			int id = i+1;
			for(int j=0;j<s.length()-qmax;j++)
			{
				String substring=s.substring(j,j+qmax);
				insert(root,substring,id,j);
			}
			for(int k=s.length()-qmax;k<s.length()-qmin+1;k++)
			{
				String substring=s.substring(k,s.length());
				insert(root,substring,id,k);
			}

		}
	}
	
	public boolean search(Node root,String s)
	{
		char[]chrs=s.toLowerCase().toCharArray();
		for(int i=0;i<chrs.length;i++)
		{
			int index=chrs[i]-'a';
			if(root.children[index]==null)
			{
				return false;
			}
			if(i==chrs.length-1&&root.children[index].isLeaf==false)
				return false;
			root=root.children[index];
		}
		return true;
	}
	
	public Node getroot()
	{
		return root;
	}
	
}

