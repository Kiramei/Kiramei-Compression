package com.kiramei;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.ProgressMonitorInputStream;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

public class Luscompression {
	/**
	 * @author Kiramei
	 */
	/*
	 * Define the variables which was used to calculate the compression ratio.
	 */
	public static long before, after;
	/*
	 * define the variable of pattern.
	 */
	private static int pat;

	public static void main(String[] args) throws Exception {
		comp();
	}
	/*
	 * The method comp is to call the method of choosing pattern, call the method of
	 * get file to run, call the method of compressing to run, call the method of
	 * showing the compression ratio and finally save the zip.
	 */

	public static void comp() throws Exception {
		// Call the method of choosing pattern
		getPattern();
		// Define the path of things to be compressed.
		String p;
		/*
		 * Use different methods to get path.
		 */
		if (pat == 1)
			p = getFilePath();
		else if (pat == 2)
			p = getDirectoryPath();
		else {
			System.exit(0);
			return;
		}
		int result = 0;// Define the variable to
		/*
		 * Create a file chooser and show the desktop.
		 */
		JFileChooser fc = new JFileChooser();
		FileSystemView fsv = FileSystemView.getFileSystemView();
		fc.setCurrentDirectory(fsv.getHomeDirectory());
		// Make it only save as *.zip.
		FileFilter ff = new FileNameExtensionFilter("Zip Files", "zip");
		fc.setFileFilter(ff);
		// Show the title.
		fc.setDialogTitle("Save the zip to...");
		// Show the "confirm" button.
		fc.setApproveButtonText("OK");
		// Make it only save as a file.
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		// Show the saving dialog instead of opening dialog.
		result = fc.showSaveDialog(fc);
		if (JFileChooser.APPROVE_OPTION == result) {
			// Call the method of compressing.
			Luscompression.toZip(p, new FileOutputStream("output.zip"));
			// Rename the file and change its directory.
			new File("output.zip").renameTo(new File(fc.getSelectedFile().getPath() + ".zip"));
			// Get the size of compressed file.
			after = new File(fc.getSelectedFile().getPath() + ".zip").length();
			// Call method to show the compression ratio.
			showMessage();
		}
		System.exit(0);
	}

	private static void compress(File sourceFile, ZipOutputStream zos, String name) throws Exception {
		// Define the bytes array which'll be read.
		byte[] buf = new byte[2 * 1024];
		if (!sourceFile.isDirectory()) {
			zos.putNextEntry(new ZipEntry(name));
			int len;
			// Show the progress bar.
			ProgressMonitorInputStream pmi = new ProgressMonitorInputStream(null, "Packaging...",
					new FileInputStream(sourceFile));
			// Read and write.
			while ((len = pmi.read(buf)) != -1) {
				zos.write(buf, 0, len);
			}
			zos.closeEntry();
			pmi.close();
		} else {
			// Get files in path.
			File[] listFiles = sourceFile.listFiles();
			if (listFiles == null || listFiles.length == 0) {
				// Write the entry.
				zos.putNextEntry(new ZipEntry(name + "/"));
				zos.closeEntry();
			} else {
				// Keep the structure.
				for (File file : listFiles) {
					// Call the method itself to compress.
					compress(file, zos, name + "/" + file.getName());
				}
			}
		}
	}

	private static void toZip(String srcDir, OutputStream out) throws RuntimeException {
		ZipOutputStream zos = null;
		try {
			// Start Compressing->Open the ZipStream
			zos = new ZipOutputStream(out);
			File sourceFile = new File(srcDir);
			compress(sourceFile, zos, sourceFile.getName());
		} catch (Exception e) {
			throw new RuntimeException("Zip failed", e);
		} finally {
			if (zos != null) {
				try {
					zos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static String getFilePath() {
		// The same as comp()
		int result = 0;
		JFileChooser fc = new JFileChooser();
		FileSystemView fsv = FileSystemView.getFileSystemView();
		fc.setCurrentDirectory(fsv.getHomeDirectory());
		fc.setDialogTitle("Please choose file...");
		fc.setApproveButtonText("OK");
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);// Make it only choose files.
		// Show the opening dialog instead of saving dialog.
		result = fc.showOpenDialog(fc);
		if (JFileChooser.APPROVE_OPTION == result) {
			// Get the size of original file.
			before = fc.getSelectedFile().length();
			return fc.getSelectedFile().getPath();
		}
		System.exit(0);
		return "";
	}

	public static String getDirectoryPath() {
		// The same as comp()
		int result = 0;
		JFileChooser fc = new JFileChooser();
		FileSystemView fsv = FileSystemView.getFileSystemView();
		fc.setCurrentDirectory(fsv.getHomeDirectory());
		fc.setDialogTitle("Please choose directory...");
		fc.setApproveButtonText("OK");
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);// Make it only choose directories.
		// Show the opening dialog instead of saving dialog.
		result = fc.showOpenDialog(fc);
		if (JFileChooser.APPROVE_OPTION == result) {
			// Get the size of original directory.
			for (int i = 0; i < new File(fc.getSelectedFile().getPath()).listFiles().length; i++) {
				before += new File(fc.getSelectedFile().getPath()).listFiles()[i].length();
			}
			return fc.getSelectedFile().getPath();
		}
		System.exit(0);
		return "";
	}

	public static void getPattern() {
		/*
		 * This method is to let the user choose the type of things they want to
		 * compress. It define the window and return value of different circumstances.
		 */
		Object[] options = { "A file", "A directory", "Cancel" };
		int m = JOptionPane.showOptionDialog(null, "Choose the pattern to compress.", "Pattern",
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,null, options, options[0]);
		pat = m + 1;
	}

	/*
	 * Show the compression ratio.
	 */
	public static void showMessage() {
		double d = (double) after / (double) before;
		JOptionPane.showMessageDialog(null, "The compression ratio is: 1 : " + d, "Compression ratio",
				JOptionPane.INFORMATION_MESSAGE);
	}
}
