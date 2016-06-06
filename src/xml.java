package mgm.hellflamer.xml;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.util.Scanner;
import java.io.File;
import java.io.FileWriter;

public class xml{
	public static void main(String[] args) throws Exception{
		Scanner fileList=null;
		Scanner reader=null;
		FileWriter writer=null;
		try{
			String inFileName=".";
			String outFileName=".";
			int minParts=2;
			int maxParts=9;
			int parts=0;
			int i;
			for(String arg:args){
				try{
					switch(arg.substring(0,2)){
						case "-h":
							System.out.println("Usage:\n\tSplitter [-h] [-i:input file or dir] [-o:output file or dir] [-n:number of parts]");
							return;
						case "-i":
							inFileName=arg.substring(3);
							break;
						case "-o":
							outFileName=arg.substring(3);
							break;
/*
больше свитчей при необходимости
*/
					}
				}catch(Exception e){e.printStackTrace();}
			}
			System.out.println("\nUse -h for more info");
			String[] fileListName;
			File inFile=new File(inFileName);
			if(!inFile.exists())inFile=new File(".");
			while(!inFile.isFile()){
				inFileName=inFile.getCanonicalPath()+'\\';
				fileListName=inFile.list();
				i=1;
				System.out.println("\n  0\t..");
				try{
					for(String s:fileListName){
						System.out.println("  "+(i++)+"\t"+s);
					}
				}catch(Exception e){e.printStackTrace();}
				System.out.println("\nSelect (0"+((i==1)?"":"-"+(i-1))+"):");
				i=inCon(0,i-1)-1;
				inFileName+=(i==-1)?"..":fileListName[i];
				inFile=new File(inFileName);
				System.out.println("\nSelected: '"+inFile.getName()+"'");
			}
			i=0;
/*
сюда писать загрузку шаблона
*/
			File outFile=new File(outFileName);
			if(!outFile.exists()){
				outFileName=inFile.getCanonicalPath();
				outFile=new File(outFileName);
			}
			if(!outFile.isFile())outFileName=outFile.getCanonicalPath()+'\\'+inFile.getName();
			parts=0;
			try{
				outFile=new File(outFileName+"."+parts++);
				outFile.createNewFile();
			}catch(Exception e){
				System.out.println("Bad destination: '"+outFileName+"', set default.");
				outFileName=inFile.getCanonicalPath();
				outFile=new File(outFileName+"."+parts++);
				outFile.createNewFile();
			}
/*
пишем здесь файл
*/
		}catch(Exception e){
			System.out.println("Something goes wrong.(O_o )\n"+e.getMessage());
		}finally{
			if(fileList!=null)fileList.close();
			if(reader!=null)reader.close();
			if(writer!=null)writer.close();
		}
		return;
	}

	private static int inCon (int min, int max){
		int i=0;
		String s="";
		boolean b=true;
		Scanner inStr=new Scanner(System.in);
		while(b){
			try{
				s=inStr.nextLine();
				i=Integer.parseInt(s);
			}catch(Exception e){i=-1;}
			if(i>=min&&i<=max){
				b=false;
			}
			else System.out.println("Incorrect input, try again");
		}
		return i;
	}
} 