package tetris_2;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
	
	Brain.Move optimalMove;
	
	GeneticTetris(int width, int height) {
		super(width, height);
	}
	public java.awt.Container createControlPanel() {
		Container panel = super.createControlPanel();
		if (testMode) speed.setValue(100);	// max for test mode
		return panel;
	}
	public void tick(int verb) {
		
		if (verb == DOWN){
			if (count != brainCount){
				board.undo();
				brainCount = count;
				optimalMove = brainInst.bestMove(board, currentPiece, HEIGHT , optimalMove);
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
				//super.tick(DROP);
				super.tick(DOWN);
			}
		}
		
		super.tick(verb);
	}
	
	public void startGame()
	{
		// load new features into brain
		brainInst.loadBrain(populationBrains.population[currentBrainIndex]);
		super.startGame();
	}
	public void stopGame()
	{
		super.stopGame();
		if (++currentBrainIndex < populationBrains.population.length)
		{
			super.startGame();
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
		
		
		tetris.populationBrains = new Population(10,4);
		tetris.populationBrains.randPop();
	}
}
