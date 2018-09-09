import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class Fenetre extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;
	private ClassLoader loader = Main.class.getClassLoader();
	private JFrame JF = new JFrame();
	private JLabel ccimage, cctext, downloadImage, downloadStatus;
	private JButton updateButton;
	private JProgressBar progressBar;
	private int downloadSize = 1, downloadedBytes = 0;
	private int width = 400, height = 175;
	private int posX = 0, posY = 0;
	private boolean downloadStarted = false, finished = false;
	
	public Fenetre() {
		
		
		
		// ------------------------------------------ Window settings ------------------------------------------
    	Dimension dim = Toolkit.getDefaultToolkit().getScreenSize(); // To center the window
    	JF.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);      
    	JF.setResizable(true);
    	JF.setUndecorated(true);
    	JF.setLayout(new BorderLayout());
    	JF.setShape(new RoundRectangle2D.Double(0, 0, width, height, 20, 20)); // Set rounded corners
    	JF.setSize(width, height);
    	JF.setLocation(dim.width/2-width/2, dim.height/2-height/2); // Center window
    	JF.add(this, BorderLayout.CENTER); // Add panel
    	// ------------------------------------------ Window settings ------------------------------------------
    	
    	
    	
    	
    	
    	    	
    	// ------------------------------------- Drag undecorated window -------------------------------------
    	JF.addMouseListener(new MouseAdapter() {
    	   public void mousePressed(MouseEvent e) {
    	      posX = e.getX();
    	      posY = e.getY();
    	   }
    	});
    	JF.addMouseMotionListener(new MouseAdapter() {
    	     public void mouseDragged(MouseEvent evt) {
    			//sets frame position when mouse dragged			
    			JF.setLocation (evt.getXOnScreen()-posX,evt.getYOnScreen()-posY);					
    	     }
    	});
    	// ------------------------------------- Drag undecorated window -------------------------------------
    	
    	
    	
    	
    	    	
    	// ------------------------------ CCleaner logo ------------------------------
    	ImageIcon img = new ImageIcon (loader.getResource("resources/logo.png"));
    	ccimage = new JLabel(img);
    	ccimage.setBounds(47, 10, 80, 80);
    	// ------------------------------ CCleaner logo ------------------------------
    	
    	
    	
    	
    	   	
    	// --------------------------- Download background image ---------------------------
    	ImageIcon img2 = new ImageIcon (loader.getResource("resources/download.png"));
    	downloadImage = new JLabel(img2);
    	downloadImage.setBounds(75, 105, 250, 60);
    	// --------------------------- Download background image ---------------------------
    	
    	
    	
    	
    	
    	// ---------------------------------------------- CCleaner updater text ----------------------------------------------
    	cctext = new JLabel("<html><h1>CCleaner Updater</h1></html>");
    	cctext.setBounds(145, 25, (int) cctext.getPreferredSize().getWidth(), (int) cctext.getPreferredSize().getHeight());
    	// ---------------------------------------------- CCleaner updater text ----------------------------------------------
    	
    	
    	
    	
    	
    	// --------------------------------- Update button ---------------------------------
    	updateButton = new JButton("<html><h3 style='color:black;'>Update</h3></html>");
    	updateButton.setFocusable(false);
    	updateButton.setBackground(Color.GRAY);
    	updateButton.setBorder(null);
    	updateButton.setBounds(134, 120, 134, 32);
    	updateButton.addActionListener(this);
    	// --------------------------------- Update button ---------------------------------
    	
    	
    	
    	
    	
    	// ----------------------------------------- Download percentage text -----------------------------------------
    	downloadStatus = new JLabel("<html><h3 style'color:black;'>downloading</h3></html>", SwingConstants.CENTER);
    	downloadStatus.setBounds(134, 120, 134, 32);
    	downloadStatus.setVisible(false);
    	// ----------------------------------------- Download percentage text -----------------------------------------
    	
    	
    	
    	
    	
    	// --------------- Progress bar ---------------
    	progressBar = new JProgressBar();
    	progressBar.setBounds(134, 120, 134, 32);
    	progressBar.setBackground(Color.LIGHT_GRAY);
    	progressBar.setForeground(Color.GRAY);
    	progressBar.setBorder(null);
    	progressBar.setVisible(false);
    	// --------------- Progress bar ---------------
    	
    	
    	
    	
    	
    	// --- Adding components ---
    	setLayout(null);
    	add(ccimage);
    	add(cctext);
    	add(progressBar);
    	add(downloadImage);
    	add(downloadStatus);
    	add(updateButton);
    	// --- Adding components ---
    	
    	

    	JF.setVisible(true); // Setting window visible
    	setComponentZOrder(downloadStatus, 0); // Setting download text in front
    	
    	
    	
	}
	

	
	public void update() {	// The whole update process
		try {
	
			
			
			// ---------------------------------------------- Fetching download URL ----------------------------------------------
			updateButton.setText("<html><h3 style='color:black;'>Fetching URL...</h3></html>"); // Set text to feching URL
			
			String siteUrl = "https://www.ccleaner.com/ccleaner/download/standard"; // Download page URL
			String saveDir = System.getProperty("user.home") + "\\Desktop\\ccsetup.exe"; // File saving location
			String downloadUrl = "";
			
			WebClient webClient = new WebClient(); // Create WebClient
			HtmlPage page = webClient.getPage(siteUrl); // Load CCleaner download page
			HtmlAnchor downloadButton = page.getAnchorByText("start the download"); // Get the download button
			downloadUrl = ""+downloadButton.getHrefAttribute(); // Set new URL with download button URL
			webClient.close(); // Close WebClient
			// ---------------------------------------------- Fetching download URL ----------------------------------------------
			
			
			
			
			
			// ----------------------- Downloading update file -----------------------
			URLConnection connection = new URL(downloadUrl).openConnection(); // Open connection
			InputStream input = connection.getInputStream();
			byte[] buffer = new byte[16384];
			downloadSize = connection.getContentLength(); // Get file size
			int bufferReadSize;

			downloadStarted = true; // Set download started for progress bar
			downloadStatus.setVisible(true); // Set percentage text visible
			progressBar.setVisible(true); // Set progress bar visible
			
			OutputStream output = new FileOutputStream(new File(saveDir)); // Set saving location
			while ((bufferReadSize = input.read(buffer)) >= 0) { // Start download
			    output.write(buffer, 0, bufferReadSize);
			    downloadedBytes += bufferReadSize;
			}
			
			input.close();
			output.close();	
			// ----------------------- Downloading update file -----------------------
			
			
			
			Process myappProcess = Runtime.getRuntime().exec("powershell.exe Start-Process "+ saveDir +" /S -Wait -verb RunAs"); // Installing update
			myappProcess.waitFor();
			
	        File file = new File(saveDir); 
	          
	        if (file.delete()) { 
	            System.out.println("File deleted successfully"); 
	        } else { 
	            System.out.println("Failed to delete the file"); 
	        } 
			
			finished = true; // Set installation finished
			downloadStatus.setVisible(false);
			progressBar.setVisible(false);
			updateButton.setVisible(true);
			updateButton.setText("<html><h3 style='color:black;'>Close</h3></html>"); // Set update button as a close button
			
			
			
		} catch (IOException | FailingHttpStatusCodeException e) {} catch (InterruptedException e) {}			
	}

	
	
	
	// ----------------------- Rounding function -----------------------
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();
	    long factor = (long) Math.pow(10, places);
	    value = value * factor;
	    long tmp = Math.round(value);
	    return (double) tmp / factor;
	}
	// ----------------------- Rounding function -----------------------
	
	
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == updateButton) {
			
			
			
			updateButton.setVisible(false); // Hide update button to avoid mutliple clicks
			downloadStatus.setVisible(true);
			downloadStatus.setText("<html><h3 style='color:black;'>Fetching url...</h3></html>");
			
			
			
			if (finished) { // If installation is finished then if updateButton clicked close the program
				System.exit(0);
			} else { // If installation haven't been started then when button is clicked
				
				
				
				// --- Start updating process ---
				Thread thread = new Thread() {
					public void run(){
						update();
					}
				};
				thread.start();			
				// --- Start updating process ---
				
				
				
				
				
				// ---------------------------------- Start progress bar updating thread ----------------------------------
				ScheduledExecutorService serverScheduler = Executors.newScheduledThreadPool(1);
				Thread updateThread = new Thread() {
					public void run() {		
						if (downloadStarted) {
							double percentage = (double) downloadedBytes / downloadSize * 100; // downloaded / filesize * 100			
							percentage = round(percentage, 2); // Round to 2 decimals
							downloadStatus.setText("<html><h3 style='color:black;'>"+ percentage +"%</h3></html>");
							progressBar.setValue((int) percentage); // Set progress bar value
							JF.repaint();
							JF.revalidate();
							if (percentage == 100) { // If percentage reach 100 mean download is finished
								System.out.println("Percentage thread interrupted");
								downloadStatus.setText("<html><h3 style='color:black;'>Installation...</h3></html>");
								serverScheduler.shutdown(); // Interrupt sheduler
								this.interrupt(); // Interrupt thread
							}
						}
					}
				};
				serverScheduler.scheduleAtFixedRate(updateThread, 0, 200, TimeUnit.MILLISECONDS); // 5 update each second
				// ---------------------------------- Start progress bar updating thread ----------------------------------
				
				
				
			}	
		}
	}
}
