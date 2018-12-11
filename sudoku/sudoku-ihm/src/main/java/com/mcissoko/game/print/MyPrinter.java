package com.mcissoko.game.print;

import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class MyPrinter {

	private Label printJobStatusLabel;
	
	
	public MyPrinter() {
		super();
	}


	public void print(Node node) 
	{
		// Define the Job Status Message
		printJobStatusLabel.textProperty().unbind();
		printJobStatusLabel.setText("Mise en place de l'impression ...");
		
		// Create a printer job for the default printer
		PrinterJob job = PrinterJob.createPrinterJob();
		
		if (job != null && job.showPrintDialog(node.getScene().getWindow())) {
			// Show the printer job status
			printJobStatusLabel.textProperty().bind(job.jobStatusProperty().asString());
						
			Printer printer = job.getPrinter();
			PageLayout pageLayout = printer.createPageLayout(Paper.A4,  PageOrientation.PORTRAIT, Printer.MarginType.HARDWARE_MINIMUM);

			boolean success = job.printPage(pageLayout, node);
			if (success) {
				job.endJob();
			} else {
				// Write Error Message
				printJobStatusLabel.textProperty().unbind();
				printJobStatusLabel.setText("Echec d'impression.");
			}
			
		} else {
			// Write Error Message
			printJobStatusLabel.setText("Vous avez annuler l'impression.");
		}
	}


	public Label getPrintJobStatusLabel() {
		return printJobStatusLabel;
	}


	public void setPrintJobStatusLabel(Label printJobStatusLabel) {
		this.printJobStatusLabel = printJobStatusLabel;
	}


	
	
}
