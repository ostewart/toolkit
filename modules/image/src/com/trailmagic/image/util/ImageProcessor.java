package com.trailmagic.image.util;

import com.trailmagic.image.*;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import javax.activation.FileDataSource;
import java.io.File;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.FileInputStream;

public class ImageProcessor {
    private Image m_image;
    private static InputStream s_inStream;
    private static PrintStream s_outStream;
    private static PrintStream s_errStream;
    private static BufferedReader s_inReader;

    static {
        s_inStream = System.in;
        s_outStream = System.out;
        s_errStream = System.err;
        s_inReader = new BufferedReader(new InputStreamReader(s_inStream));
    }

    public ImageProcessor() {
    }

    public void setImage(Image image) {
        m_image = image;
    }

    public Image getImage() {
        return m_image;
    }

    public ImageManifestation makeImageManifestation() {
        ImageManifestation im = new ImageManifestation();

        return im;
    }

    public static String getStringFromUser(String prompt) {
        try {
            s_outStream.print(prompt + " ");
            return s_inReader.readLine();
        } catch (IOException e) {
            s_errStream.println("WTF?");
            s_errStream.println(e.getMessage());
            return null;
        }
    }

    public static long getLongFromUser(String prompt) {
        while ( true ) {
            try {
                s_outStream.print(prompt + " ");
                return Long.parseLong(s_inReader.readLine());
            } catch (NumberFormatException e) {
                s_errStream.println("Invalid entry. Please enter " +
                                    "a long integer.");
            } catch (IOException e) {
                s_errStream.println("WTF?");
                s_errStream.println(e.getMessage());
            }
        }
    }

    public static int getIntegerFromUser(String prompt) {
        while ( true ) {
            try {
                s_outStream.print(prompt + " ");
                return Integer.parseInt(s_inReader.readLine());
            } catch (NumberFormatException e) {
                s_errStream.println("Invalid entry. Please enter " +
                                    "an integer.");
            } catch (IOException e) {
                s_errStream.println("WTF?");
                s_errStream.println(e.getMessage());
            }
        }
    }

    public static final void main(String[] args) {
        if ( args.length < 1 ) {
            System.err.println("Usage: ImageProcessor <filename> " +
                               "[<filename> ...]");
            System.exit(1);
        }
        
        for (int i = 0; i < args.length; i++ ) {
            File file = new File(args[i]);
            if ( !file.canRead() ) {
                System.err.println("Error: file " + args[i] +
                                   " does not exist or is unreadable.");
                continue;
            }

            System.out.println("File: " + args[i]);
            try {
            FileInputStream fis = new FileInputStream(file);
            // find out the mime type?
            FileDataSource fds = new FileDataSource(file);
            String mimeType = fds.getContentType();
            System.out.println("Content-type: " + mimeType);
            } catch (FileNotFoundException e) {
                System.err.println("WTF?");
                System.err.println(e.getMessage());
            }

            long id = getLongFromUser("Enter Image ID:");
            try {
                Image image = Image.findById(id);
                System.out.println("Name: " + image.getName());
                System.out.println("Display Name: " + image.getDisplayName());
                HibernateUtil.closeSession();
            } catch (HibernateException e) {
                System.err.println(e.getMessage());
            }

        }
    }
}
