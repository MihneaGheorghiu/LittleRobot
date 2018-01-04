//Gheorghiu Mihnea
//Grupa 344C2
/*HOMEWORK Nr. 7
Implement the movement of a robot in a labyrinth based on a genetic algoritm. 
Robot can move N, S, E, V, his behaviour being determind by the list of possible moves in the labyrinth. 
MAZE has one single entry point from where the little robot start it'movement and one exit (where the robot must go to) 
The sooner the robot will reach the exit, with fewer moves, the better the score will be. 
Implement the movement of the robot in the labyrinth (including graphical).
*/
import java.util.*;
import java.io.*;
import java.awt.*;
import javax.swing.*;
import java.awt.geom.*;
import java.awt.image.*;
import javax.imageio.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.text.*;

class Robotel extends JFrame{
	boolean deseneaza=false;
	BufferedImage trail = null;
	BufferedImage flag = null;
	int x,y,oldx,oldy;
	int rd,cl;//deplasarea curenta
	BufferedImage robot = null;
	boolean start=false;//pt a retine startul
	boolean finish=false;//find exit?
	Random randomize=new Random();//folosit la generarea de nr aleatoare
	int nr_indivizi=50;
	int indivizi[][];
	int calitate[];
	char instante[][][];//pe unde se afla in maze fiecare cromozom
	//pozitiile curente in labirint ale indivizilor
	int rdcrt[];
	int colcrt[];
	int mutari[];//contorizare nr mutari pt fiecare individ
	
	public Robotel(){//constructorul clasei
	super("Tema Semestru SPTR");
	//start algoritm genetic
    indivizi=new int[nr_indivizi][11];
		//(pasul 1) initializam indivizii
		for(int i = 0; i < nr_indivizi; i++ ){
			for(int j = 0; j < 11; j++ ){
				indivizi[i][j] =Math.abs(randomize.nextInt() % 4);
			}
		}
		mutari=new int[nr_indivizi];
		for(int i=0;i<nr_indivizi;i++){
			mutari[i]=0;
		}
		//facem nr_indivizi de mazeuri
		instante=new char[nr_indivizi][dim][dim];
		for(int c=0;c<nr_indivizi;c++)
			for(int i=0;i<dim;i++)
				for(int j=0;j<dim;j++)
					instante[c][i][j]=MAZE[i][j];
					
	
					
					
		//initializare pozitie curenta pt fiecare individ
		rdcrt=new int[nr_indivizi];
		colcrt=new int[nr_indivizi];
		for(int c=0;c<nr_indivizi;c++)
			for(int i=0;i<dim;i++)
				for(int j=0;j<dim;j++)
					if(instante[c][i][j]=='S'){
						rdcrt[c]=i;
						colcrt[c]=j;
					
				}
		//pas 2
		mutare();
		calitate=new int[nr_indivizi];
		for(int i=0;i<nr_indivizi;i++){
			calitate[i]=mutari[i]+solve(instante[i]);
		}
	//ordonare indivizi	
	for(int i=0;i<nr_indivizi-1;i++)
		for(int j=i+1;j<nr_indivizi;j++){
			if(calitate[i]>calitate[j]){//nu sunt ordonati
				char caux;
				
				int aux;
				//interschimbam calitate
				aux=calitate[j];
				calitate[j]=calitate[i];
				calitate[i]=aux;
				//interschimbam populatiile
				for(int k=0;k<11;k++){
					aux=indivizi[j][k];
					indivizi[j][k]=indivizi[i][k];
					indivizi[i][k]=aux;
				}
				//interschimbam instantele de labirinturi		
				for(int k=0;k<dim;k++)
					for(int l=0;l<dim;l++){
					caux=instante[j][k][l];
					instante[j][k][l]=instante[i][k][l];
					instante[i][k][l]=caux;
				}
				
				//interschimbam vectorul mutari
				aux=mutari[j];
				mutari[j]=mutari[i];
				mutari[i]=aux;
				//interschimbam vectorul rdcrt
				aux=rdcrt[j];
				rdcrt[j]=rdcrt[i];
				rdcrt[i]=aux;
				//interschimbam vectorul colcrt
				aux=colcrt[j];
				colcrt[j]=colcrt[i];
				colcrt[i]=aux;
				
			}
		}
	
		
		
		
		
		
		
		
		
		int solutie=-1;
		
		//lasam sa ruleze cel putin 5 secunde
		long t1 = System.currentTimeMillis(); //initializam timer
		long t2 = System.currentTimeMillis(); // stop timing
		
		
		
		
		while((solutie<0)&&((t2-t1)<5000)){
    		
    		//Selectia
    		double elitism=0.05;//pastram mereu cei mai buni 4 % cromozomi
    		int nr_elitism=(int)(elitism*nr_indivizi);
    		if(nr_elitism==0)
    			nr_elitism=1;
    	
    		double mutatie = 0.2;
    		int nr_mutatie=(int) (mutatie * (nr_indivizi - nr_elitism));
    		int copii[][]=new int[nr_indivizi - nr_elitism][11];
		
    
    		//procesul de genetic crossover
    		for(int i = nr_elitism; i < nr_indivizi-1; i ++ ){
				//random cut off
			
				int cutoff = Math.abs(randomize.nextInt() % 11); 
		
				//parintii ii alegem in mod aleator
				int parinte1 = (Math.abs(randomize.nextInt() % nr_indivizi));
				int parinte2 = (Math.abs(randomize.nextInt() % nr_indivizi));
				//realizam incrucisarea, obtinem doi copii din doi parinti
				for(int j = 0; j < cutoff; j++ ){
					copii[i-nr_elitism][j] = indivizi[parinte1][j];
				copii[i-nr_elitism+1][j] = indivizi[parinte2][j];
				}
				for(int j = cutoff; j < 11; j++ ){
					copii[i-nr_elitism][j] = indivizi[parinte2][j];
					copii[i-nr_elitism+1][j] = indivizi[parinte1][j];
			
				}
			i++;
		
		}

    	//efectuam mutatia pentru un anumit nr de copii
    	int mutatii=0;
    	while(mutatii<nr_mutatie){
    		//alegem random un copil
    		int nr=Math.abs(randomize.nextInt() % (nr_indivizi-nr_elitism));
    		//alegem unde facem mutatia
    		int where=Math.abs(randomize.nextInt() % 11);
    		//alegem vaaloarea pt mutatie
    		int what=Math.abs(randomize.nextInt() % 4);
    		copii[nr][where]=what;
    		mutatii++;
    		
    	}
    	
    
    	
    	for(int i = nr_elitism; i < nr_indivizi; i ++ ){
    		calitate[i]=0;//luam de la capat
    		for(int k=0;k<11;k++)
    			indivizi[i][k]=copii[i-nr_elitism][k];
    		
    		for(int k=0;k<dim;k++)
			for(int l=0;l<dim;l++)
				instante[i][k][l]=MAZE[k][l];
		
			mutari[i]=1;
			
    		
    		
    		for(int k=0;k<dim;k++)
			for(int l=0;l<dim;l++)
				if(instante[i][k][l]=='S'){
					rdcrt[i]=k;
					colcrt[i]=l;
					
				}
    	
    	}
    	
    	
    	
    	
    	
    	
    	//5 mutari pt fiecare individ
    	for(int m=0;m<5;m++)
    		mutare();
    	
    	//calcul calitate
    	
    	  calitate=new int[nr_indivizi];
	
		for(int i=0;i<nr_indivizi;i++){
			calitate[i]=mutari[i]+solve(instante[i]);
		}
	//ordonare indivizi	
	for(int i=0;i<nr_indivizi-1;i++)
		for(int j=i+1;j<nr_indivizi;j++){
			if(calitate[i]>calitate[j]){//nu sunt ordonati
				
				char caux;
				int aux;
				
				//interschimbam calitate
				aux=calitate[j];
				calitate[j]=calitate[i];
				calitate[i]=aux;
				//interschimbam populatiile
				for(int k=0;k<11;k++){
					aux=indivizi[j][k];
					indivizi[j][k]=indivizi[i][k];
					indivizi[i][k]=aux;
				}
				//interschimbam instantele de labirinturi		
				for(int k=0;k<dim;k++)
					for(int l=0;l<dim;l++){
					caux=instante[j][k][l];
					instante[j][k][l]=instante[i][k][l];
					instante[i][k][l]=caux;
				}
				
				//interschimbam vectorul mutari
				aux=mutari[j];
				mutari[j]=mutari[i];
				mutari[i]=aux;
				//interschimbam vectorul rdcrt
				aux=rdcrt[j];
				rdcrt[j]=rdcrt[i];
				rdcrt[i]=aux;
				//interschimbam vectorul colcrt
				aux=colcrt[j];
				colcrt[j]=colcrt[i];
				colcrt[i]=aux;
				
			}
		}
	  
       		
    	t2 = System.currentTimeMillis(); // stop timing
    }//end loop algoritm_genetic
    
		
	//afisam cea mai buna solutie pt a fi ilustrata grafic
	
	System.out.println("Calitate = "+calitate[0]);
	//mazeul pt cel mai bun individ
		for(int i=0;i<dim;i++){
			for(int j=0;j<dim;j++)
				System.out.print(instante[0][i][j]+" ");;
			System.out.print("\n");
		}
	
	//afisare caracteristici individ
	System.out.print("Individ:");
	for(int i=0;i<11;i++)
		System.out.print(i+"="+indivizi[0][i]+" ");
	//stop algoritm genetic (stim care e cel mai bun individ)
    
    setResizable(false);
	setSize(40*dim,42*dim);
	setVisible(true);
	setDefaultCloseOperation(EXIT_ON_CLOSE);	
	center(this);
	repaint();
	}
	//mutare pentru fiecare individ
	public void mutare(){
		
	
    	for(int i=0;i<nr_indivizi;i++){
    	
    		//situatia 0 
    		if(((instante[i][rdcrt[i]-1][colcrt[i]]=='.')|(instante[i][rdcrt[i]-1][colcrt[i]]=='E'))&&((instante[i][rdcrt[i]][colcrt[i]-1]=='.')|(instante[i][rdcrt[i]][colcrt[i]-1]=='E'))&&((instante[i][rdcrt[i]+1][colcrt[i]]=='.')|(instante[i][rdcrt[i]+1][colcrt[i]]=='E'))&&((instante[i][rdcrt[i]][colcrt[i]+1]=='.')|(instante[i][rdcrt[i]][colcrt[i]+1]=='E')))
    		{	
    		
    		if(indivizi[i][0]==0){// sus
    			
    			instante[i][rdcrt[i]][colcrt[i]]='.';
    			mutari[i]=mutari[i]+1;
    			rdcrt[i]=rdcrt[i]-1;
    		
    			instante[i][rdcrt[i]][colcrt[i]]='S';	
    		}
    		else if(indivizi[i][0]==1){// stanga
    			
    			instante[i][rdcrt[i]][colcrt[i]]='.';
    			mutari[i]=mutari[i]+1;
    			colcrt[i]=colcrt[i]-1;
    			
    			instante[i][rdcrt[i]][colcrt[i]]='S';	
    		}
    		else if(indivizi[i][0]==2){// jos
    			
    			instante[i][rdcrt[i]][colcrt[i]]='.';
    			mutari[i]=mutari[i]+1;
    			rdcrt[i]=rdcrt[i]+1;
    			
    			instante[i][rdcrt[i]][colcrt[i]]='S';	
    		}
    		else if(indivizi[i][0]==3){// dreapta
    			
    			instante[i][rdcrt[i]][colcrt[i]]='.';
    			mutari[i]=mutari[i]+1;
    			colcrt[i]=colcrt[i]+1;
    			
    			instante[i][rdcrt[i]][colcrt[i]]='S';	
    		}
    		   		   			
    		
    		}//end situatia 0
    		
    		//situatia 1 
    		else if((instante[i][rdcrt[i]-1][colcrt[i]]=='*')&&((instante[i][rdcrt[i]][colcrt[i]-1]=='.')|(instante[i][rdcrt[i]][colcrt[i]-1]=='E'))&&((instante[i][rdcrt[i]+1][colcrt[i]]=='.')|(instante[i][rdcrt[i]+1][colcrt[i]]=='E'))&&((instante[i][rdcrt[i]][colcrt[i]+1]=='.')|(instante[i][rdcrt[i]][colcrt[i]+1]=='E')))
    		{	
    		  			
    		if(indivizi[i][1]==1){// stanga
    			
    			instante[i][rdcrt[i]][colcrt[i]]='.';
    			mutari[i]=mutari[i]+1;
    			colcrt[i]=colcrt[i]-1;
    			
    				
    			instante[i][rdcrt[i]][colcrt[i]]='S';	
    		}
    		else if(indivizi[i][1]==2){// jos
    			
    			instante[i][rdcrt[i]][colcrt[i]]='.';
    			mutari[i]=mutari[i]+1;
    			rdcrt[i]=rdcrt[i]+1;
    			
    				
    			instante[i][rdcrt[i]][colcrt[i]]='S';	
    		}
    		else if(indivizi[i][1]==3){// dreapta
    			
    			instante[i][rdcrt[i]][colcrt[i]]='.';
    			mutari[i]=mutari[i]+1;
    			colcrt[i]=colcrt[i]+1;
    			
    				
    			instante[i][rdcrt[i]][colcrt[i]]='S';	
    		}
    		
    		
    		
    		}//end situatia 1
    		
    		//situatia 2
    		else if(((instante[i][rdcrt[i]-1][colcrt[i]]=='.')|(instante[i][rdcrt[i]-1][colcrt[i]]=='E'))&&(instante[i][rdcrt[i]][colcrt[i]-1]=='*')&&((instante[i][rdcrt[i]+1][colcrt[i]]=='.')|(instante[i][rdcrt[i]+1][colcrt[i]]=='E'))&&((instante[i][rdcrt[i]][colcrt[i]+1]=='.')|(instante[i][rdcrt[i]][colcrt[i]+1]=='E')))
    		{	
    	   			
    		if(indivizi[i][2]==0){// sus
    			
    			instante[i][rdcrt[i]][colcrt[i]]='.';
    			mutari[i]=mutari[i]+1;
    			rdcrt[i]=rdcrt[i]-1;
    			
    				
    			instante[i][rdcrt[i]][colcrt[i]]='S';	
    		}
    		else if(indivizi[i][2]==2){// jos
    			
    			instante[i][rdcrt[i]][colcrt[i]]='.';
    			mutari[i]=mutari[i]+1;
    			rdcrt[i]=rdcrt[i]+1;
    			
    				
    			instante[i][rdcrt[i]][colcrt[i]]='S';	
    		}
    		else if(indivizi[i][2]==3){
    			
    			instante[i][rdcrt[i]][colcrt[i]]='.';
    			mutari[i]=mutari[i]+1;
    			colcrt[i]=colcrt[i]+1;
    			
    				
    			instante[i][rdcrt[i]][colcrt[i]]='S';	
    		}
    		
    		
    		
    		
    		}//end situatia 2
    		
    		//situatia 3 
    		else if(((instante[i][rdcrt[i]-1][colcrt[i]]=='.')|(instante[i][rdcrt[i]-1][colcrt[i]]=='E'))&&((instante[i][rdcrt[i]][colcrt[i]-1]=='.')|(instante[i][rdcrt[i]][colcrt[i]-1]=='E'))&&(instante[i][rdcrt[i]+1][colcrt[i]]=='*')&&((instante[i][rdcrt[i]][colcrt[i]+1]=='.')|(instante[i][rdcrt[i]][colcrt[i]+1]=='E')))
    		{	
    		   			
    		if(indivizi[i][3]==0){// sus
    			
    			instante[i][rdcrt[i]][colcrt[i]]='.';
    			mutari[i]=mutari[i]+1;
    			rdcrt[i]=rdcrt[i]-1;
    			
    				
    			instante[i][rdcrt[i]][colcrt[i]]='S';	
    		}
    		else if(indivizi[i][3]==1){// stanga
    			
    			instante[i][rdcrt[i]][colcrt[i]]='.';
    			mutari[i]=mutari[i]+1;
    			colcrt[i]=colcrt[i]-1;
    			//
    				
    			instante[i][rdcrt[i]][colcrt[i]]='S';	
    		}
    		
    		else if(indivizi[i][3]==3){// dreapta
    			
    			instante[i][rdcrt[i]][colcrt[i]]='.';
    			mutari[i]=mutari[i]+1;
    			colcrt[i]=colcrt[i]+1;
    			//
    				
    			instante[i][rdcrt[i]][colcrt[i]]='S';	
    		}
    		}//end situatia 3
    		
    		
    		//situatia 4 
    		else if(((instante[i][rdcrt[i]-1][colcrt[i]]=='.')|(instante[i][rdcrt[i]-1][colcrt[i]]=='E'))&&((instante[i][rdcrt[i]][colcrt[i]-1]=='.')|(instante[i][rdcrt[i]][colcrt[i]-1]=='E'))&&((instante[i][rdcrt[i]+1][colcrt[i]]=='.')|(instante[i][rdcrt[i]+1][colcrt[i]]=='E'))&&(instante[i][rdcrt[i]][colcrt[i]+1]=='*'))
    		{	
    	
    		if(indivizi[i][4]==0){// sus
    			
    			instante[i][rdcrt[i]][colcrt[i]]='.';
    			mutari[i]=mutari[i]+1;
    			rdcrt[i]=rdcrt[i]-1;
    			//
    				
    			instante[i][rdcrt[i]][colcrt[i]]='S';	
    		}
    		else if(indivizi[i][4]==1){// stanga
    			
    			instante[i][rdcrt[i]][colcrt[i]]='.';
    			mutari[i]=mutari[i]+1;
    			colcrt[i]=colcrt[i]-1;
    			//
    				
    			instante[i][rdcrt[i]][colcrt[i]]='S';	
    		}
    		else if(indivizi[i][4]==2){// jos
    			
    			instante[i][rdcrt[i]][colcrt[i]]='.';
    			mutari[i]=mutari[i]+1;
    			rdcrt[i]=rdcrt[i]+1;
    			//
    				
    			instante[i][rdcrt[i]][colcrt[i]]='S';	
    		}
    		   			
    		
    		}//end situatia 4
    		
    		//situatia 5 
    		else if((instante[i][rdcrt[i]-1][colcrt[i]]=='*')&&((instante[i][rdcrt[i]][colcrt[i]-1]=='.')|(instante[i][rdcrt[i]][colcrt[i]-1]=='E'))&&(instante[i][rdcrt[i]+1][colcrt[i]]=='*')&&((instante[i][rdcrt[i]][colcrt[i]+1]=='.')|(instante[i][rdcrt[i]][colcrt[i]+1]=='E')))
    		{	
    		
    		if(indivizi[i][5]==1){// stanga
    			
    			instante[i][rdcrt[i]][colcrt[i]]='.';
    			mutari[i]=mutari[i]+1;
    			colcrt[i]=colcrt[i]-1;
    			//
    				
    			instante[i][rdcrt[i]][colcrt[i]]='S';	
    		}
    		else if(indivizi[i][5]==3){// dreapta
    			
    			instante[i][rdcrt[i]][colcrt[i]]='.';
    			mutari[i]=mutari[i]+1;
    			colcrt[i]=colcrt[i]+1;
    			//
    				
    			instante[i][rdcrt[i]][colcrt[i]]='S';	
    		} 			
    		
    		}//end situatia 5
    		
    		//situatia 6 
    		else if(((instante[i][rdcrt[i]-1][colcrt[i]]=='.')|(instante[i][rdcrt[i]-1][colcrt[i]]=='E'))&&(instante[i][rdcrt[i]][colcrt[i]-1]=='*')&&((instante[i][rdcrt[i]+1][colcrt[i]]=='.')|(instante[i][rdcrt[i]+1][colcrt[i]]=='E'))&&(instante[i][rdcrt[i]][colcrt[i]+1]=='*'))
    		{	
    	   			
    		if(indivizi[i][6]==0){// sus
    			
    			instante[i][rdcrt[i]][colcrt[i]]='.';
    			mutari[i]=mutari[i]+1;
    			rdcrt[i]=rdcrt[i]-1;
    			//
    				
    			instante[i][rdcrt[i]][colcrt[i]]='S';	
    		}
    		
    		else if(indivizi[i][6]==2){// jos
    			
    			instante[i][rdcrt[i]][colcrt[i]]='.';
    			mutari[i]=mutari[i]+1;
    			rdcrt[i]=rdcrt[i]+1;
    			//
    				
    			instante[i][rdcrt[i]][colcrt[i]]='S';	
    		}
    		
    		}//end situatia 6
    		
    		//situatia 7 
    		else if((instante[i][rdcrt[i]-1][colcrt[i]]=='*')&&(instante[i][rdcrt[i]][colcrt[i]-1]=='*')&&((instante[i][rdcrt[i]+1][colcrt[i]]=='.')|(instante[i][rdcrt[i]+1][colcrt[i]]=='E'))&&((instante[i][rdcrt[i]][colcrt[i]+1]=='.')|(instante[i][rdcrt[i]][colcrt[i]+1]=='E')))
    		{	
    	   			
    		if(indivizi[i][7]==2){// jos
    			
    			instante[i][rdcrt[i]][colcrt[i]]='.';
    			mutari[i]=mutari[i]+1;
    			rdcrt[i]=rdcrt[i]+1;
    			//
    				
    			instante[i][rdcrt[i]][colcrt[i]]='S';	
    		}
    		else if(indivizi[i][7]==3){// dreapta
    			
    			instante[i][rdcrt[i]][colcrt[i]]='.';
    			mutari[i]=mutari[i]+1;
    			colcrt[i]=colcrt[i]+1;
    			//
    				
    			instante[i][rdcrt[i]][colcrt[i]]='S';	
    		}
    		}//end situatia 7
    		
    		//situatia 8 
    		else if(((instante[i][rdcrt[i]-1][colcrt[i]]=='.')|(instante[i][rdcrt[i]-1][colcrt[i]]=='E'))&&(instante[i][rdcrt[i]][colcrt[i]-1]=='*')&&(instante[i][rdcrt[i]+1][colcrt[i]]=='*')&&((instante[i][rdcrt[i]][colcrt[i]+1]=='.')|(instante[i][rdcrt[i]][colcrt[i]+1]=='E')))
    		{	
    	
    		if(indivizi[i][8]==0){// sus
    			
    			instante[i][rdcrt[i]][colcrt[i]]='.';
    			mutari[i]=mutari[i]+1;
    			rdcrt[i]=rdcrt[i]-1;
    			//
    				
    			instante[i][rdcrt[i]][colcrt[i]]='S';	
    		}
    		
    		else if(indivizi[i][8]==3){// dreapta
    			
    			instante[i][rdcrt[i]][colcrt[i]]='.';
    			mutari[i]=mutari[i]+1;
    			colcrt[i]=colcrt[i]+1;
    			//
    				
    			instante[i][rdcrt[i]][colcrt[i]]='S';	
    		}   			
    		
    		}//end situatia 8
    		
    		//situatia 9 
    		else if(((instante[i][rdcrt[i]-1][colcrt[i]]=='.')|(instante[i][rdcrt[i]-1][colcrt[i]]=='E'))&&((instante[i][rdcrt[i]][colcrt[i]-1]=='.')|(instante[i][rdcrt[i]][colcrt[i]-1]=='E'))&&(instante[i][rdcrt[i]+1][colcrt[i]]=='*')&&(instante[i][rdcrt[i]][colcrt[i]+1]=='*'))
    		{	
    	   			
    		if(indivizi[i][9]==0){// sus
    			
    			instante[i][rdcrt[i]][colcrt[i]]='.';
    			mutari[i]=mutari[i]+1;
    			rdcrt[i]=rdcrt[i]-1;
    			//
    				
    			instante[i][rdcrt[i]][colcrt[i]]='S';	
    		}
    		
    		else if(indivizi[i][9]==3){// dreapta
    			
    			instante[i][rdcrt[i]][colcrt[i]]='.';
    			mutari[i]=mutari[i]+1;
    			colcrt[i]=colcrt[i]+1;
    			//
    				
    			instante[i][rdcrt[i]][colcrt[i]]='S';	
    		}
    		}//end situatia 9
    		
    		
    		//situatia 10 
    		else if((instante[i][rdcrt[i]-1][colcrt[i]]=='*')&&((instante[i][rdcrt[i]][colcrt[i]-1]=='.')|(instante[i][rdcrt[i]][colcrt[i]-1]=='E'))&&((instante[i][rdcrt[i]+1][colcrt[i]]=='.')|(instante[i][rdcrt[i]+1][colcrt[i]]=='E'))&&(instante[i][rdcrt[i]][colcrt[i]+1]=='*'))
    		{	
    		  			
    		if(indivizi[i][10]==1){// stanga
    			
    			instante[i][rdcrt[i]][colcrt[i]]='.';
    			mutari[i]=mutari[i]+1;
    			colcrt[i]=colcrt[i]-1;
    			//
    				
    			instante[i][rdcrt[i]][colcrt[i]]='S';	
    		}
    		else if(indivizi[i][10]==2){// jos
    			
    			instante[i][rdcrt[i]][colcrt[i]]='.';
    			mutari[i]=mutari[i]+1;
    			rdcrt[i]=rdcrt[i]+1;
    			//
    				
    			instante[i][rdcrt[i]][colcrt[i]]='S';	
    		}
    	
    		}//end situatia 10
    	
    	
    	}
    }//end void mutare
	
	
	
	public void paint(Graphics g){
        
	Dimension size=getSize();
	g.translate(0,26);
	//desenare background
	BufferedImage img = null;
        try {
            String imageFileName = "background.jpg";
            img = ImageIO.read(new File(imageFileName));
        } catch (IOException e) {
        }      
    g.drawImage(img,0,0,size.width,size.height,Color.BLACK,null);                    
	
	
	
	
	try {
            String imageFileName = "zid.jpg";
            img = ImageIO.read(new File(imageFileName));
        } 
    catch (IOException e) {
    }
    
   
    //robot
	try {
            String imageFileName = "robot.jpg";
            robot = ImageIO.read(new File(imageFileName));
        } 
    catch (IOException e) {
    }
     //sfarsit
    BufferedImage goal = null;
     
	try {
            String imageFileName = "END.jpg";
            goal = ImageIO.read(new File(imageFileName));
        } 
    catch (IOException e) {
    }
   
    try {
            String imageFileName = "trail.jpg";
            trail = ImageIO.read(new File(imageFileName));
        } 
    catch (IOException e) {
    }
    
    try {
            String imageFileName = "start.jpg";
            flag = ImageIO.read(new File(imageFileName));
        } 
    catch (IOException e) {
    }        
    
    
    int xz=0,yz=0;
    
    for(int i=0;i<dim;i++){
    	for(int j=0;j<dim;j++){
    		if(MAZE[i][j]=='*')
    	   		g.drawImage(img,xz,yz,40,40,Color.BLACK,null);      
			else if(MAZE[i][j]=='S'){
				oldx=xz;
				oldy=yz+26;
				rd=i;
				cl=j;
				g.drawImage(robot,xz,yz,40,40,Color.BLACK,null);      
			}
			else if(MAZE[i][j]=='E')
				g.drawImage(goal,xz,yz,40,40,Color.BLACK,null);
			xz=xz+40;
		}
		xz=0;
		yz=yz+40;
	}
	
	update(g);
    
    }
    
    
    
	public void update(Graphics g){
	
	if(deseneaza){
		
		
		g.drawImage(robot,x,y,40,40,Color.BLACK,null);
			
		if(start){
			g.drawImage(flag,oldx,oldy,40,40,Color.BLACK,null);
			start=false;
		}
		else
			g.drawImage(trail,oldx,oldy,40,40,Color.BLACK,null);
		
		//
		if(MAZE[rd][cl]=='E'){
		
				JOptionPane.showMessageDialog(this, "Ati ajuns la final! Felicitari"); 
				
				
				

				dispose();
		}		
				
		oldx=x;
		oldy=y;	
		deseneaza=false;
		repaint();
		}
		
		
	else{//aici vom urma pasii din cel mai bun individ
	
	
	//timer(cate 2 secunde intre deplasari)
    long t1 = System.currentTimeMillis(); //initializam timer
	long t2 = System.currentTimeMillis(); // stop timing
  
   	while((t2-t1)<2000)
   		t2 = System.currentTimeMillis();
  	
   	
   	//vedem in care din cele 11 situatii ne aflam
   	//situatia 0 = liber peste tot
    if(((MAZE[rd-1][cl]=='.')|(MAZE[rd-1][cl]=='E'))&&((MAZE[rd][cl-1]=='.')|(MAZE[rd][cl-1]=='E'))&&((MAZE[rd+1][cl]=='.')|(MAZE[rd+1][cl]=='E'))&&((MAZE[rd][cl+1]=='.')|(MAZE[rd][cl+1]=='E')))
    		{	
    		if(indivizi[0][0]==0){// sus
    			
    			deplasare_sus();
    		}
    		else if(indivizi[0][0]==1){// stanga
    			
    			deplasare_stanga();	
    		}
    		else if(indivizi[0][0]==2){// jos
    			
    			deplasare_jos();	
    		}
    		else if(indivizi[0][0]==3){// dreapta
    			
    			deplasare_dreapta();	
    		}
    		   		   			
    		
    	}//end situatia 0
    	
    	//situatia 1
    	else if((MAZE[rd-1][cl]=='*')&&((MAZE[rd][cl-1]=='.')|(MAZE[rd][cl-1]=='E'))&&((MAZE[rd+1][cl]=='.')|(MAZE[rd+1][cl]=='E'))&&((MAZE[rd][cl+1]=='.')|(MAZE[rd][cl+1]=='E')))
    		{	
    		if(indivizi[0][1]==1){// stanga
    			
    			deplasare_stanga();	
    		}
    		else if(indivizi[0][1]==2){// jos
    			
    			deplasare_jos();	
    		}
    		else if(indivizi[0][1]==3){// dreapta
    			
    			deplasare_dreapta();	
    		}
    		   		   			
    		
    	}//end situatia 1	 	 
    	
		//situatia 2
    	else if(((MAZE[rd-1][cl]=='.')|(MAZE[rd-1][cl]=='E'))&&(MAZE[rd][cl-1]=='*')&&((MAZE[rd+1][cl]=='.')|(MAZE[rd+1][cl]=='E'))&&((MAZE[rd][cl+1]=='.')|(MAZE[rd][cl+1]=='E')))
    		{	
    		if(indivizi[0][2]==0){// sus
    			
    			deplasare_sus();
    		}
    		else if(indivizi[0][2]==2){// jos
    			
    			deplasare_jos();	
    		}
    		else if(indivizi[0][2]==3){// dreapta
    			
    			deplasare_dreapta();	
    		}
    		   		   			
    		
    	}//end situatia 2
    	
		//situatia 3
    	else if(((MAZE[rd-1][cl]=='.')|(MAZE[rd-1][cl]=='E'))&&((MAZE[rd][cl-1]=='.')|(MAZE[rd][cl-1]=='E'))&&(MAZE[rd+1][cl]=='*')&&((MAZE[rd][cl+1]=='.')|(MAZE[rd][cl+1]=='E')))
    		{	
    		if(indivizi[0][3]==0){// sus
    			
    			deplasare_sus();
    		}
    		else if(indivizi[0][3]==1){// stanga
    			
    			deplasare_stanga();	
    		}
    	
    		else if(indivizi[0][3]==3){// dreapta
    			
    			deplasare_dreapta();	
    		}
    		   		   			
    		
    	}//end situatia 3
		
		//situatia 4
    	else if(((MAZE[rd-1][cl]=='.')|(MAZE[rd-1][cl]=='E'))&&((MAZE[rd][cl-1]=='.')|(MAZE[rd][cl-1]=='E'))&&((MAZE[rd+1][cl]=='.')|(MAZE[rd+1][cl]=='E'))&&(MAZE[rd][cl+1]=='*'))
    		{	
    		if(indivizi[0][4]==0){// sus
    			
    			deplasare_sus();
    		}
    		else if(indivizi[0][4]==1){// stanga
    			
    			deplasare_stanga();	
    		}
    		else if(indivizi[0][4]==2){// jos
    			
    			deplasare_jos();	
    		}
    	
    		   		   			
    		
    	}//end situatia 4
    	
    	//situatia 5
    	else if((MAZE[rd-1][cl]=='*')&&((MAZE[rd][cl-1]=='.')|(MAZE[rd][cl-1]=='E'))&&(MAZE[rd+1][cl]=='*')&&((MAZE[rd][cl+1]=='.')|(MAZE[rd][cl+1]=='E')))
    		{	
    		if(indivizi[0][5]==1){// stanga
    			
    			deplasare_stanga();	
    		}
    		else if(indivizi[0][5]==3){// dreapta
    			
    			deplasare_dreapta();	
    		}
    		   		   			
    		
    	}//end situatia 5
    	
    	
    	//situatia 6
    	else if(((MAZE[rd-1][cl]=='.')|(MAZE[rd-1][cl]=='E'))&&(MAZE[rd][cl-1]=='*')&&((MAZE[rd+1][cl]=='.')|(MAZE[rd+1][cl]=='E'))&&(MAZE[rd][cl+1]=='*'))
    		{	
    		if(indivizi[0][6]==0){// sus
    			
    			deplasare_sus();
    		}
    		
    		else if(indivizi[0][6]==2){// jos
    			
    			deplasare_jos();	
    		}
    		
    		
    	}//end situatia 6
    	
    	//situatia 7
    	else if((MAZE[rd-1][cl]=='*')&&(MAZE[rd][cl-1]=='*')&&((MAZE[rd+1][cl]=='.')|(MAZE[rd+1][cl]=='E'))&&((MAZE[rd][cl+1]=='.')|(MAZE[rd][cl+1]=='E')))
    		{	
    		if(indivizi[0][7]==2){// jos
    			
    			deplasare_jos();	
    		}
    		else if(indivizi[0][7]==3){// dreapta
    			
    			deplasare_dreapta();	
    		}
    		   		   			
    		
    	}//end situatia 7
    	
    	
    	//situatia 8
    	else if(((MAZE[rd-1][cl]=='.')|(MAZE[rd-1][cl]=='E'))&&(MAZE[rd][cl-1]=='*')&&(MAZE[rd+1][cl]=='*')&&((MAZE[rd][cl+1]=='.')|(MAZE[rd][cl+1]=='E')))
    		{	
    		if(indivizi[0][8]==0){// sus
    			
    			deplasare_sus();
    		}
    		
    		else if(indivizi[0][8]==3){// dreapta
    			
    			deplasare_dreapta();	
    		}
    		   		   			
    		
    	}//end situatia 8
    	
    	//situatia 9
    	else if(((MAZE[rd-1][cl]=='.')|(MAZE[rd-1][cl]=='E'))&&((MAZE[rd][cl-1]=='.')|(MAZE[rd][cl-1]=='E'))&&(MAZE[rd+1][cl]=='*')&&(MAZE[rd][cl+1]=='*'))
    		{	
    		if(indivizi[0][9]==0){// sus
    			
    			deplasare_sus();
    		}
    		else if(indivizi[0][9]==1){// stanga
    			
    			deplasare_stanga();	
    		}
    		
    		   		   			
    		
    	}//end situatia 9
    	
    	//situatia 10
    	else if((MAZE[rd-1][cl]=='*')&&((MAZE[rd][cl-1]=='.')|(MAZE[rd][cl-1]=='E'))&&((MAZE[rd+1][cl]=='.')|(MAZE[rd+1][cl]=='E'))&&(MAZE[rd][cl+1]=='*'))
    		{	
    		if(indivizi[0][10]==1){// stanga
    			
    			deplasare_stanga();	
    		}
    		else if(indivizi[0][10]==2){// jos
    			
    			deplasare_jos();	
    		}
    	 			
    		
    	}//end situatia 10
    	
    	
    	
    	
    	
    	
    		
		
		
	}
	
	
		
	}//end update
	
	public static void center (Window w){
	Dimension ws = w.getSize(), 
	ss = Toolkit.getDefaultToolkit().getScreenSize();
	int newX = (ss.width - ws.width) / 2;
	int newY = (ss.height- ws.height)/ 2;
	w.setLocation(newX, newY);
  	}
  	
  	//deplasari
	public void deplasare_sus(){
		
		
		
		
		
		if((MAZE[rd-1][cl]=='.')|(MAZE[rd-1][cl]=='S')|(MAZE[rd-1][cl]=='E')){
			if(MAZE[rd][cl]=='S')
				start=true;
			
			
			deseneaza=true;
			x=oldx;
			y=oldy-40;
			rd--;
		}
		
		
		repaint();	
	}
	public void deplasare_jos(){
		
		
		if((MAZE[rd+1][cl]=='.')|(MAZE[rd+1][cl]=='S')|(MAZE[rd+1][cl]=='E')){
			if(MAZE[rd][cl]=='S')
				start=true;
			deseneaza=true;
			x=oldx;
			y=oldy+40;
			rd++;
		}
		
		
		repaint();	
	}
	public void deplasare_dreapta(){
		
		if((MAZE[rd][cl+1]=='.')|(MAZE[rd][cl+1]=='S')|(MAZE[rd][cl+1]=='E')){
			if(MAZE[rd][cl]=='S')
				start=true;
		
			deseneaza=true;
			x=oldx+40;
			y=oldy;
			cl++;
		}
		
		
		repaint();	
	}
	public void deplasare_stanga(){
		if((MAZE[rd][cl-1]=='.')|(MAZE[rd][cl-1]=='S')|(MAZE[rd][cl-1]=='E')){
			if(MAZE[rd][cl]=='S')
				start=true;
		
			
			deseneaza=true;
			x=oldx-40;
			y=oldy;
			cl--;
		}
		
		
		repaint();	
	}
	
	
	
	

	
	
	static int dim=15;
	static char MAZE[][]=new char[dim][dim];
	
	
	
	
	//functie ce calculeaza distanta pana la iesire
	public static int solve(char[][] maze){
	
	
	
	String lab;
	//punem labirintul in stringul lab
	
	
	char linie[]=new char[dim*dim];
	int k=0;
	for(int i=0;i<dim;i++){
		for(int j=0;j<dim;j++){
			linie[k++]=maze[i][j];	
		
		}
	}
		
	lab=new String(linie);
	String maz=new String(lab);
	int r=dim;
	int c=dim;
	
  	String[] ar = new String[r*c];
  	int s = maz.indexOf('S'), e = maz.indexOf('E');
  		ar[s] = ""+(char)(s);
		lp:  for(int i=0;i<r*c;i++){
    			for(int x=0;x<r*c;x++){
      				if(ar[x]!=null){
        				if(x==e) break lp;
        				if(x%c!=0 && ar[x-1]==null && maz.charAt(x-1)!='*') ar[x-1]=ar[x]+(char)(x-1);
        				if(x%c!=c-1 && ar[x+1]==null && maz.charAt(x+1)!='*') ar[x+1]=ar[x]+(char)(x+1);
        				if(x/c!=0 && ar[x-c]==null && maz.charAt(x-c)!='*') ar[x-c]=ar[x]+(char)(x-c);
        				if(x/c!=r-1 && ar[x+c]==null && maz.charAt(x+c)!='*') ar[x+c]=ar[x]+(char)(x+c);
      				}
    			}
  		}
   		if(ar[e]==null) {
  			System.out.println("Cannot reach");
  			return -1;
  		}
  		else{
    		return ar[e].length()-1;
  	}

}


	
		
	public static void main(String []arg)throws IOException{

	RandomAccessFile file=new RandomAccessFile("maze.txt","r");
	
	String line;
	int j=0;
	while((line=file.readLine())!=null){
		for(int i=0;i<line.length();i++)
			MAZE[j][i]=line.charAt(i);
		j++;
	}	
	file.close();
	Robotel robo=new Robotel();
	}
}