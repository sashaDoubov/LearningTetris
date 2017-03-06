package tetris_2;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import tetris_2.JTetris;

public class JBrainTetris extends JTetris {
	
	protected JCheckBox activeBrain;
	LameBrain brainInst = new LameBrain();
	private int brainCount = 0;
	
	Brain.Move optimalMove = new Brain.Move();
	
	JBrainTetris(int width, int height) {
		super(width, height);
	}

	public void tick(int verb) {
//		if (verb == DOWN){
//			if (count != brainCount){
//				optimalMove = brainInst.bestMove(board, currentPiece, HEIGHT , optimalMove);
//				board.undo();
//				brainCount = count;
//			}
//		
//		if (optimalMove != null)
//		{
//			if (!currentPiece.equals(optimalMove.piece))
//			{
//				currentPiece = currentPiece.nextRotation();
//			}
//			
//			if (currentX  < optimalMove.x){
//				currentX++;
//			}
//			else if (currentX > optimalMove.x){
//				currentX--;
//			}
//		}
//		}
//		
//		super.tick(verb);
		
		 switch (verb) {
	        case LEFT:
	            ;
	        case RIGHT:
	            ;
	        case DROP:
	            ;
	        case ROTATE:
	            super.tick(verb);
	            return;
	        case DOWN:
	            break;
	        }

	            if (count != brainCount) {
	                board.undo();
	                brainCount = count;
	                optimalMove = brainInst
	                        .bestMove(board, currentPiece, HEIGHT, optimalMove);
	            }

	            if (optimalMove == null) {
	                return;
	            }

	            if (!optimalMove.piece.equals(currentPiece)) {
	                super.tick(ROTATE);
	            }

	            if (optimalMove.x > currentX) {
	                super.tick(RIGHT);
	            } else if (optimalMove.x < currentX) {
	                super.tick(LEFT);
	            } else if (optimalMove.piece.equals(currentPiece)) {
	                super.tick(DROP);
	                super.tick(DOWN);
	            }


	        super.tick(DOWN);
	}
	public java.awt.Container createControlPanel() {
		java.awt.Container panel = Box.createVerticalBox();

		// COUNT
		countLabel = new JLabel("0");
		panel.add(countLabel);
		
		// TIME 
		timeLabel = new JLabel(" ");
		panel.add(timeLabel);

		panel.add(Box.createVerticalStrut(12));
		
		// START button
		startButton = new JButton("Start");
		panel.add(startButton);
		startButton.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				startGame();
			}
		});
		
		// STOP button
		stopButton = new JButton("Stop");
		panel.add(stopButton);
		stopButton.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stopGame();
			}
		});
		
		// add checkbox
		
		activeBrain = new JCheckBox("Brain");
		panel.add(activeBrain);
		if (testMode)
			activeBrain.setSelected(true);
		
		enableButtons();
		
		JPanel row = new JPanel();
		
		// SPEED slider
		panel.add(Box.createVerticalStrut(12));
		row.add(new JLabel("Speed:"));
		speed = new JSlider(0, 200, 75);	// min, max, current
		speed.setPreferredSize(new Dimension(100,15));
		if (testMode) speed.setValue(200);	// max for test mode
		
		updateTimer();
		row.add(speed);
		
		panel.add(row);
		speed.addChangeListener( new ChangeListener() {
			// when the slider changes, sync the timer to its value
			public void stateChanged(ChangeEvent e) {
				updateTimer();
			}
		});
		
		
		return(panel);
	}
	
	/**
	 Creates a Window,
	 installs the JTetris or JBrainTetris,
	 checks the testMode state,
	 install the controls in the WEST.
	*/
	public static void main(String[] args)
	
	{
		JFrame frame = new JFrame("Tetris 2000");
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
		JBrainTetris tetris = new JBrainTetris(WIDTH*pixels+2, (HEIGHT+TOP_SPACE)*pixels+2);
		
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
