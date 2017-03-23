package tetris_2;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import tetris_2.JTetris;

public class GeneticTetris extends JTetris {
	
	protected JCheckBox activeBrain;
    protected JTextField brainText;
    protected JLabel status;
    protected JButton loadBrain;

	GeneticBrain brainInst = new GeneticBrain();
	Population populationBrains;
	private int currentBrainIndex = 0;
	private int brainCount = 0;
	//private Random originalRandom;
	
	private int populationGeneration = 0;
	Brain.Move optimalMove;
	private boolean firstInGeneration;
	private int populationSize;
	
	GeneticTetris(int width, int height) {
		super(width, height);
		populationSize = 100;
		
		populationBrains = new Population(populationSize,4);
		populationBrains.randPop();
		
		// wipe text file
		PrintWriter pw = null;
		try {
			pw = new PrintWriter("jam.txt");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		pw.close();
		
		random = new Random(0);
	}
	
	public java.awt.Container createControlPanel() {
		Container panel = super.createControlPanel();

		JPanel row = new JPanel();
		
		// SPEED slider
		//panel.add(Box.createVerticalStrut(12));
		row.add(new JLabel("Score"));
		panel.add(row);
		
		if (testMode) speed.setValue(100);	// max for test mode
		return panel;
		
	}
	public void tick(int verb) {
		if (verb == DOWN){
			if (count != brainCount){
				board.undo();
				brainCount = count;
				optimalMove = brainInst.bestMove(board, currentPiece, HEIGHT+TOP_SPACE, optimalMove);
			}
		
		if (optimalMove == null)
			return;
			
			if (!currentPiece.equals(optimalMove.piece))
			{
				super.tick(ROTATE);
			}
			
			if (optimalMove.x  >  currentX){
				super.tick(RIGHT);
			}
			else if (optimalMove.x < currentX){
				super.tick(LEFT);
			}
			else if (optimalMove.piece.equals(currentPiece))
			{
				super.tick(DROP);
				super.tick(DOWN);
			}
		}
		
		super.tick(verb);
	}
	
	public void startGame()
	{
		// load new features into brain
		brainInst.loadBrain(populationBrains.population[currentBrainIndex]);

		// cheap way to reset the board state
		board = new Board(WIDTH, HEIGHT + TOP_SPACE);
		
		// draw the new board state once
		repaint();
		
		count = 0;
		gameOn = true;

		enableButtons();
		timeLabel.setText(" ");
		addNewPiece();
		timer.start();
		startTime = System.currentTimeMillis();
	}
	public void stopGame()
	{		
		super.stopGame();
		random = new Random((int)(populationGeneration / 3) * (1 << 50));
		//random = new Random(0);
		//populationBrains.scores[currentBrainIndex] = board.getScore();
		populationBrains.population[currentBrainIndex].setScore(count);
		currentBrainIndex++;
		
		if (currentBrainIndex < populationBrains.population.length){
			startGame();
		}
		else if (populationGeneration < 300)
		{
			// keep for each population
			currentBrainIndex = 0;
			
			if (testMode){
				try{
					BufferedWriter out = new BufferedWriter(new FileWriter("jam.txt",true));
					out.newLine();
					out.write("Generation " + populationGeneration);
					
					for (int i = 0; i < populationBrains.population.length; i++)
					{
						out.newLine();
						for (int j = 0; j < populationBrains.population[i].getFeatLength(); j++)
						{
							out.write(Double.toString(populationBrains.population[i].features[j]) + " ");
						}
						out.newLine();
						out.write("score " + populationBrains.population[i].getScore());
					}
					double sum = 0;
					for (int i = 0; i < populationSize; i++)
					{
						if (populationBrains.population[i].getScore() == 0)
							System.out.println("WHHAT");
						sum += populationBrains.population[i].getScore();
					}
					 sum = sum/(double)populationSize;
					System.out.println(sum);
					out.newLine();
					out.write("g_score = " + sum);
					
					out.close();
				}
				catch(IOException e){}
			}
			
			int eliteOffset = (int)(0.15 * populationSize);
			System.out.println("elite" + eliteOffset);
			Population populationBrainsTemp = new Population(populationSize,4);
			
			Chromosome[] tempChr = populationBrains.population;
			Arrays.sort(tempChr);
			
			for (int i = 0; i < eliteOffset; i++)
			{
				//System.out.println("sorted" +  tempChr[i].getScore());
				populationBrainsTemp.population[i] = tempChr[i];
			}
			
			populationBrains.fitSum();
			for (int i = eliteOffset; i < populationBrains.population.length; i++)
			{
				Chromosome par1;
				Chromosome par2;
				par1 = populationBrains.rouletteSelection();
				par2 = populationBrains.rouletteSelection();
				
				//populationBrainsTemp.population[i] = Chromosome.randomCrossOver(par1, par2);
				populationBrainsTemp.population[i] = Chromosome.singleCrossOver(par1, par2);
				populationBrainsTemp.population[i].mutate(-0.05, 0.05);
			}
			
			populationBrains = populationBrainsTemp;
			populationGeneration++;
			startGame();
		}
		else
		{
			int eliteScore = -1;
			int eliteIndex = -1;
			for (int i = 0; i < populationBrains.population.length; i++)
			{
				if (populationBrains.population[i].getScore() > eliteScore)
				{
					eliteScore = populationBrains.population[i].getScore();
					eliteIndex = i;
				}
			}
			System.out.println("eliteScore: " + eliteScore);
			
			System.out.println("elite features: ");
			for (int i = 0; i < populationBrains.population[eliteIndex].features.length; i++)
			{
				System.out.println(populationBrains.population[eliteIndex].features[i] + " ");
			}
		}
	}
	/**
	 Creates a Window,
	 installs the JTetris or JBrainTetris,
	 checks the testMode state,
	 install the controls in the WEST.
	*/

	public static void main(String[] args)
	
	{
		
		JFrame frame = new JFrame("Genetic Tetris");
		JComponent container = (JComponent)frame.getContentPane();
		container.setLayout(new BorderLayout());
               
       // Set the metal look and feel
       try {
           UIManager.setLookAndFeel(
               UIManager.getCrossPlatformLookAndFeelClassName() );
       }
       catch (Exception ignored) {}
		
		// Could create a JTetris or JBrainTetris here
		final int pixels = 16;
		// create instance of jbrain instead
		GeneticTetris tetris = new GeneticTetris(WIDTH*pixels+2, (HEIGHT+TOP_SPACE)*pixels+2);
		
		container.add(tetris, BorderLayout.CENTER);


		if (args.length != 0 && args[0].equals("test")) {
			tetris.testMode = true;
		}
		
		Container panel = tetris.createControlPanel();
		// Add the quit button last so it's at the bottom
		panel.add(Box.createVerticalStrut(12));
		JButton quit = new JButton("Quit");
		panel.add(quit);
		quit.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		
		
		container.add(panel, BorderLayout.EAST);
		frame.pack();
		frame.setVisible(true);

		// Quit on window close
		frame.addWindowListener(
			new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					System.exit(0);
				}
			}
		);
	}
}
