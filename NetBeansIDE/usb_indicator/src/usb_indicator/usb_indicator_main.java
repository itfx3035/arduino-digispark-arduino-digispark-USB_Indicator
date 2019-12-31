/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package usb_indicator;

import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.image.BufferedImage;
import java.net.URL;
import javax.imageio.ImageIO;
import java.awt.AWTException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.JFrame;
import javax.mail.*;
import java.util.*;
import javax.mail.search.BodyTerm;

/**
 *
 * @author nemo
 */


public class usb_indicator_main extends javax.swing.JFrame {

    int StopAll=0;
    int ThreadStarted=0;
    TrayIcon trayIcon;
    SystemTray tray;
    BufferedImage ic;
    ExecutorService exec1;
    ExecutorService exec2;
    ExecutorService exec3;
    String jar_folder;
    String digiusb_send_bin;
    String digiusb_send_param;
    
    // cron vars
    String cron_filename;
    String[] cfgCronHH = new String[1000];
    String[] cfgCronMM = new String[1000];
    String[] cfgCronSS = new String[1000];
    Integer[] cfgCronTS = new Integer[1000];
    //String[] cfgCronType = new String[1000];
    String[] cfgCronName = new String[1000];
    int CronRecordsCount=0;
    
    // events log
    String[] evLog = new String[1000];
    Integer[] evLogRecType = new Integer[1000];
    int EventsCount=0;
    
    /**
     * Creates new form usb_indicator_main
     */
    public usb_indicator_main() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jClose = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jtaLog = new javax.swing.JTextArea();
        jTest = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jClose.setText("Close");
        jClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCloseActionPerformed(evt);
            }
        });

        jtaLog.setEditable(false);
        jtaLog.setColumns(20);
        jtaLog.setRows(5);
        jScrollPane1.setViewportView(jtaLog);

        jTest.setText("Test");
        jTest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTestActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jTest)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jClose))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 426, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 213, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jClose)
                    .addComponent(jTest))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        
        if (ThreadStarted==0)
        {
            ThreadStarted=1;            
            setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);            
            setTitle("USB indicator");
            
            // tray icon and events ========================================
            tray = SystemTray.getSystemTray();
            URL iconUrl = this.getClass().getResource("/images/icon.png");
            
            try
            {
                ic = ImageIO.read(iconUrl); 
                trayIcon = new TrayIcon(ic);
                try {
                    tray.add(trayIcon);
                } catch (AWTException e) {}                
            } catch (IOException e) {}
            
            ActionListener al = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    setVisible(true);
                }
            };
            trayIcon.addActionListener(al);
            
            MouseListener ml;
            ml = new MouseListener() {
                @Override public void mouseClicked(MouseEvent e) {
                    //System.out.println("Tray icon: Mouse clicked");
                    EventsCount=0;
                }
                @Override public void mousePressed(MouseEvent e) {}
                @Override public void mouseReleased(MouseEvent e) {}
                @Override public void mouseEntered(MouseEvent e) {}
                @Override public void mouseExited(MouseEvent e) {}
            };
            trayIcon.addMouseListener(ml);
            // =======================================================
            
            
            setVisible(false);  
            
            // get settings folder (our jar file also should be placed there)
            jar_folder= "";
            try
            {
                jar_folder = new File(usb_indicator_main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent();
            } catch (URISyntaxException use) {}            
            
            // run main threads =======================================
            exec1 = Executors.newSingleThreadExecutor();
            exec1.execute(new Runnable() {
                @Override
                public synchronized void run() 
                {
                    CommunicationThread();
                }                   
            });
            
            exec2 = Executors.newSingleThreadExecutor();
            exec2.execute(new Runnable() {
                @Override
                public synchronized void run() 
                {
                    CronThread();
                }                   
            });
            
            exec3 = Executors.newSingleThreadExecutor();
            exec3.execute(new Runnable() {
                @Override
                public synchronized void run() 
                {
                    MailThread();
                }                   
            });
            
            exec1.shutdownNow();
            exec2.shutdownNow();
            exec3.shutdownNow();
            // =======================================================
        }
    }//GEN-LAST:event_formWindowOpened

    private static String ParseParam(String input_str, String delimit, int ind)
    {
        int ln;
        int curr_section=1;
        String ret="";
        ln=input_str.length();
        for (int i=1; i<=ln; i++)
        {
            if (input_str.substring(i-1, i).equals(delimit))
            {
                curr_section++;
                continue;
            }
            if (curr_section==ind)
            {
                ret=ret+input_str.substring(i-1, i);
            }
        }
        return ret;
    };
    
            
    public void CommunicationThread(){        
        
        System.out.println("Comm thread started");
        
        digiusb_send_bin="";
        digiusb_send_bin = jar_folder.concat("\\digiusb_bin\\send.exe");
        
        while (true)
        {
            if (StopAll==1)
            {
                break;
            }
            
            try
            {
                Thread.sleep(5000);
            } catch (InterruptedException intex){};
                       
            // report events ============================
            digiusb_send_param="";
            for (int i=1;i<=EventsCount;i++)
            {
                if (evLogRecType[i]==0) // test event
                {
                    digiusb_send_param=digiusb_send_param.concat("0101-0001-");
                }
                if (evLogRecType[i]==1) // cron event
                {
                    digiusb_send_param=digiusb_send_param.concat("0101-0001-0101-0001-");
                }
                if (evLogRecType[i]==2) // green event
                {
                    digiusb_send_param=digiusb_send_param.concat("1001-0001-1001-0001-");
                }
                if (evLogRecType[i]==3) // red event
                {
                    digiusb_send_param=digiusb_send_param.concat("0011-0001-0011-0001-");
                }
                digiusb_send_param=digiusb_send_param.concat("0003-");
            }            
            try
            {    
                Process p = Runtime.getRuntime().exec(digiusb_send_bin.concat(" ").concat(digiusb_send_param));
                BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line = null;
                while ( (line = br.readLine()) != null ){                                    
                    System.out.println(line);               
                }
                br.close();                                
            } catch (IOException ioe){}            
            // ==========================================            
        }  
        
        System.out.println("Comm thread stopped");
    }
    
    public void CronThread(){        
        System.out.println("Cron thread started");
        int HaveCronFile;
        
        int cHH;
        int cMM;
        int cSS; 
        int cTS;
        int currHH;
        int currMM;
        int currSS;
        int currTS;
        
               
        cron_filename = jar_folder.concat("\\cron.txt");
        CronRecordsCount=0;
        try
        {                
            BufferedReader breader;
            FileReader freader;
            
            freader = new FileReader(cron_filename);
            breader = new BufferedReader(freader); 
            String line = breader.readLine();
            while (line != null) {
                CronRecordsCount++;
                cHH=Integer.parseInt(ParseParam(line,"|",1));
                cMM=Integer.parseInt(ParseParam(line,"|",2));
                cSS=Integer.parseInt(ParseParam(line,"|",3));
                cfgCronHH[CronRecordsCount]=ParseParam(line,"|",1);
                cfgCronMM[CronRecordsCount]=ParseParam(line,"|",2);
                cfgCronSS[CronRecordsCount]=ParseParam(line,"|",3);
                cfgCronTS[CronRecordsCount]=cHH*3600+cMM*60+cSS;
                //cfgCronType[CronRecordsCount]=ParseParam(line,"|",4);
                cfgCronName[CronRecordsCount]=ParseParam(line,"|",5);
                line = breader.readLine();
            }
            breader.close();
            freader.close();
            HaveCronFile=1;
        } catch (IOException ioe)
        {
            HaveCronFile=0;
        };        
        
        while (HaveCronFile==1)
        {            
            if (StopAll==1)
            {
                break;
            }
            // calculate new events ============================
            try
            {
                Thread.sleep(3000);
                                
                Calendar cln = Calendar.getInstance();
                currHH=cln.get(Calendar.HOUR_OF_DAY);
                currMM=cln.get(Calendar.MINUTE);
                currSS=cln.get(Calendar.SECOND);
                currTS=currHH*3600+currMM*60+currSS;
                //System.err.println(currTS);
                
                for (int i=1;i<=CronRecordsCount;i++)
                {
                    cTS=cfgCronTS[i];
                    //System.err.println(cTS);    
                    if (currTS >= cTS+3)
                    {
                        // overdue long time ago
                    }
                    else
                    {
                        if (currTS >= cTS) // overdue 
                        {
                            EventsCount++;
                            evLog[EventsCount]=cfgCronName[i];
                            evLogRecType[EventsCount]=1; // cron event
                            
                            jtaLog.append(cfgCronHH[i].concat(":"));
                            jtaLog.append(cfgCronMM[i].concat(":"));
                            jtaLog.append(cfgCronSS[i].concat(" - "));
                            jtaLog.append(cfgCronName[i].concat("\n"));
                        }
                        else
                        {
                            // upcoming event                            
                        }
                    }                               
                }
            } catch (InterruptedException intex){};         
            // ==========================================                   
        }        
        System.out.println("Cron thread stopped");
    }
    
    
    public void MailThread(){        
        System.out.println("Mail thread started");
        int HaveMailFile;
        
        String mail_server="";
        String mail_addr="";
        String mail_pass="";
        String mail_green_pattern="";
        String mail_red_pattern="";
        
        int LastGreenCount=0;
        int LastRedCount=0;
        int CurrGreenCount=0;
        int CurrRedCount=0;
        
        cron_filename = jar_folder.concat("\\mail.txt");
        CronRecordsCount=0;
        try
        {                
            BufferedReader breader;
            FileReader freader;
            
            freader = new FileReader(cron_filename);
            breader = new BufferedReader(freader); 
            mail_server = breader.readLine();
            mail_addr = breader.readLine();
            mail_pass = breader.readLine();
            mail_green_pattern = breader.readLine();
            mail_red_pattern = breader.readLine();            
            breader.close();
            freader.close();
            HaveMailFile=1;
        } catch (IOException ioe)
        {
            HaveMailFile=0;
        };        
        
        while (HaveMailFile==1)
        {            
            if (StopAll==1)
            {
                break;
            }
            // check new mail ============================
            try
            {
                Thread.sleep(60000);
                                
                try
                {
                    Session session = Session.getDefaultInstance(new Properties( ));
                    Store store = session.getStore("imaps");
                    store.connect(mail_server, 993, mail_addr, mail_pass);
                    Folder inbox = store.getFolder( "INBOX" );
                    inbox.open( Folder.READ_ONLY );
                    
                    if (mail_green_pattern.equals(""))
                    {}
                    else
                    {    
                        Message[] messages_green = inbox.search(new BodyTerm(mail_green_pattern));            
                        CurrGreenCount=messages_green.length;     
                        if (LastGreenCount!=0)
                        {
                            if (LastGreenCount!=CurrGreenCount)
                            {
                                EventsCount++;
                                evLog[EventsCount]="Green mail received";
                                evLogRecType[EventsCount]=2; // Green mail event
                                jtaLog.append("Green mail received\n");
                            }
                        }                        
                        LastGreenCount=CurrGreenCount;
                    }

                    if (mail_red_pattern.equals(""))
                    {}
                    else
                    {
                        Message[] messages_red = inbox.search(new BodyTerm(mail_red_pattern));            
                        CurrRedCount=messages_red.length;
                        if (LastRedCount!=0)
                        {
                            if (LastRedCount!=CurrRedCount)
                            {
                                EventsCount++;
                                evLog[EventsCount]="Red mail received";
                                evLogRecType[EventsCount]=3; // Red mail event
                                jtaLog.append("Red mail received\n");
                            }
                        }
                        LastRedCount=CurrRedCount;
                    }
                    

                } catch (Exception e)
                {
                    System.out.println(e.toString());
                }                              
                
            } catch (InterruptedException intex){};         
            // ==========================================                   
        }        
        System.out.println("Mail thread stopped");
    }
    
    
    private void jCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCloseActionPerformed
        // TODO add your handling code here:
        StopAll=1;
        tray.remove(trayIcon);
        dispose();
    }//GEN-LAST:event_jCloseActionPerformed

    private void jTestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTestActionPerformed
        // TODO add your handling code here:
        EventsCount++;
        evLog[EventsCount]="Test";
        evLogRecType[EventsCount]=0; // test event
    }//GEN-LAST:event_jTestActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(usb_indicator_main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(usb_indicator_main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(usb_indicator_main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(usb_indicator_main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new usb_indicator_main().setVisible(true);
            }
        });
        
        
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jClose;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton jTest;
    private javax.swing.JTextArea jtaLog;
    // End of variables declaration//GEN-END:variables
}