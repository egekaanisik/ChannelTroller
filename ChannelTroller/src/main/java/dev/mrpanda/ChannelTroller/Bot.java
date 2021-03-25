package dev.mrpanda.ChannelTroller;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import javax.security.auth.login.LoginException;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.basic.BasicScrollBarUI;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceGuildDeafenEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceGuildMuteEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;
import okhttp3.OkHttpClient;

public class Bot extends ListenerAdapter {
	
	public static String DISCORD_TOKEN = ""; // Discord token
	
	public static JFrame f = new JFrame("Initializing...");
	public static JPanel p = new JPanel();
	@SuppressWarnings("rawtypes")
	public static JList guildSelector = null;
	@SuppressWarnings("rawtypes")
	public static JList channelSelector = null;
	public static JButton sound = null;
	public static JButton change = null;
	public static JButton done = null;
	public static JButton clear = null;
	public static JButton mute = null;
	public static JButton unmute = null;
	public static JButton deafen = null;
	public static JButton undeafen = null;
	public static JButton kick = null;
	public static JTextField filename = null;
	public static JButton select = null;
	public static JButton play = null;
	public static File file = null;
	public static JFileChooser fileChooser = null;
	public static JTextField name = null;
	public static JTextArea log = null;
	public static DefaultListModel<String> channelNames = null;
	public static File blist = new File(Paths.get("").toAbsolutePath().toString() + "\\blacklist.txt");
	public static Scanner scan = null;
	public static JButton circulate = null;
	public static List<String> blacklist = new ArrayList<String>();
  
	public static void main(String[] args) throws IOException {
		p.setLayout(null);
		updateBlacklist();
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					menu();
				} catch (LoginException | InterruptedException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
    
		f.pack();
		f.getContentPane().removeAll();
		f.getContentPane().add(p);
		f.setSize(995, 430);
		f.setVisible(true);
		f.setResizable(false);
		f.setDefaultCloseOperation(3);
	}
	
	public static void updateBlacklist() throws IOException {
		if(!blist.exists())
			blist.createNewFile();
		
		blacklist.clear();
		scan = null;
		scan = new Scanner(blist);
		
		while(scan.hasNextLine()) {
			blacklist.add(scan.nextLine());
		}
	}
  
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void menu() throws LoginException, InterruptedException, IOException {
		p.removeAll();
		
		DefaultListModel<String> guildNames = getGuildNames();
		
		channelSelector = new JList();
		channelSelector.setBackground(new Color(43,68,155));
		channelSelector.setForeground(Color.WHITE);
		channelSelector.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		channelSelector.setLayoutOrientation(JList.VERTICAL);
		channelSelector.setFont(new Font("Arial", Font.PLAIN, 12));
		channelSelector.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		channelSelector.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
            	if (!e.getValueIsAdjusting()) {
            		f.setTitle("Channel Troller Control Panel ("
            				+ guilds.get(guildSelector.getSelectedIndex()).getName() +  "/"
            				+ voicechannels.get(guildSelector.getSelectedIndex()).get(channelSelector.getSelectedIndex()).getName() + ")");
            		name.setText("");
            		filename.setText("");
            		file = null;
            		filename.setVisible(false);
            		select.setVisible(false);
            		play.setVisible(false);
            		name.setVisible(false);
            		done.setVisible(false);
            		change.setEnabled(true);
            		circulate.setEnabled(true);
            		clear.setEnabled(true);
            		mute.setEnabled(true);
            		unmute.setEnabled(true);
            		sound.setEnabled(true);
            		deafen.setEnabled(true);
            		undeafen.setEnabled(true);
            		kick.setEnabled(true);
            	}
            }
        });
		
		JScrollPane channelScr = new JScrollPane(channelSelector);
		channelScr.setBounds(200,20,160,350);
		channelScr.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
			@Override
			protected void configureScrollBarColors() {
				this.thumbColor = Color.WHITE;
				this.trackColor = new Color(54,86,197);
			}
		
			@Override
			protected JButton createDecreaseButton(int orientation) {
				JButton button = super.createDecreaseButton(orientation);
				button.setBackground(Color.WHITE);
				return button;
			}
				
			@Override
			protected JButton createIncreaseButton(int orientation) {
				JButton button = super.createIncreaseButton(orientation);
				button.setBackground(Color.WHITE);
				return button;
			}
		});
		channelScr.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		channelScr.getHorizontalScrollBar().setUI(new BasicScrollBarUI() {
			@Override
			protected void configureScrollBarColors() {
				this.thumbColor = Color.WHITE;
				this.trackColor = new Color(54,86,197);
			}
		
			@Override
			protected JButton createDecreaseButton(int orientation) {
				JButton button = super.createDecreaseButton(orientation);
				button.setBackground(Color.WHITE);
				return button;
			}
				
			@Override
			protected JButton createIncreaseButton(int orientation) {
				JButton button = super.createIncreaseButton(orientation);
				button.setBackground(Color.WHITE);
				return button;
			}
		});
		channelScr.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		guildSelector = new JList(guildNames);
		guildSelector.setBackground(new Color(43,68,155));
		guildSelector.setForeground(Color.WHITE);
		guildSelector.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		guildSelector.setLayoutOrientation(JList.VERTICAL);
		guildSelector.setFont(new Font("Arial", Font.PLAIN, 12));
		guildSelector.setEnabled(false);
		guildSelector.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		guildSelector.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
            	if (!e.getValueIsAdjusting()) {
            		f.setTitle("Channel Troller Control Panel (No Channel Selected)");
            		
            		name.setText("");
            		filename.setText("");
            		file = null;
            		filename.setVisible(false);
            		select.setVisible(false);
            		play.setVisible(false);
            		name.setVisible(false);
            		done.setVisible(false);
            		change.setEnabled(false);
            		circulate.setEnabled(false);
            		clear.setEnabled(false);
            		mute.setEnabled(false);
            		unmute.setEnabled(false);
            		deafen.setEnabled(false);
            		undeafen.setEnabled(false);
            		kick.setEnabled(false);
            		sound.setEnabled(false);
            		
            		try {
            			channelNames = getChannelNames(guildSelector.getSelectedIndex());
            			channelSelector.setModel(channelNames);
            		} catch (IndexOutOfBoundsException e3) {
            			
            		}
            	}
            }
        });
		
		JScrollPane guildScr = new JScrollPane(guildSelector);
		guildScr.setBounds(20,20,160,350);
		guildScr.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
			@Override
			protected void configureScrollBarColors() {
				this.thumbColor = Color.WHITE;
				this.trackColor = new Color(54,86,197);
			}
		
			@Override
			protected JButton createDecreaseButton(int orientation) {
				JButton button = super.createDecreaseButton(orientation);
				button.setBackground(Color.WHITE);
				return button;
			}
				
			@Override
			protected JButton createIncreaseButton(int orientation) {
				JButton button = super.createIncreaseButton(orientation);
				button.setBackground(Color.WHITE);
				return button;
			}
		});
		guildScr.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		guildScr.getHorizontalScrollBar().setUI(new BasicScrollBarUI() {
			@Override
			protected void configureScrollBarColors() {
				this.thumbColor = Color.WHITE;
				this.trackColor = new Color(54,86,197);
			}
		
			@Override
			protected JButton createDecreaseButton(int orientation) {
				JButton button = super.createDecreaseButton(orientation);
				button.setBackground(Color.WHITE);
				return button;
			}
				
			@Override
			protected JButton createIncreaseButton(int orientation) {
				JButton button = super.createIncreaseButton(orientation);
				button.setBackground(Color.WHITE);
				return button;
			}
		});
		guildScr.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    
		log = new JTextArea("Activity Log:\n");
		log.setBackground(new Color(43,68,155));
		log.setForeground(Color.WHITE);
		log.setLineWrap(true);
		log.setEditable(false);
		log.setFont(new Font("Consolas", Font.PLAIN, 12));
		log.setWrapStyleWord(true);
		log.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
	
		JScrollPane logScr = new JScrollPane(log);
		logScr.setBounds(660,20,300,300);
		logScr.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
			@Override
			protected void configureScrollBarColors() {
				this.thumbColor = Color.WHITE;
				this.trackColor = new Color(54,86,197);
			}
		
			@Override
			protected JButton createDecreaseButton(int orientation) {
				JButton button = super.createDecreaseButton(orientation);
				button.setBackground(Color.WHITE);
				return button;
			}
				
			@Override
			protected JButton createIncreaseButton(int orientation) {
				JButton button = super.createIncreaseButton(orientation);
				button.setBackground(Color.WHITE);
				return button;
			}
		});
		logScr.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    
		name = new JTextField();
		name.setBounds(380, 340, 210, 30);
		name.setVisible(false);
		name.setBackground(new Color(43,68,155));
		name.setForeground(Color.WHITE);
		name.setFont(new Font("Arial", Font.PLAIN, 13));
		name.setHorizontalAlignment(JTextField.CENTER);
		name.setDocument(new JTextFieldLimit(32));
		
		circulate = new JButton("<html><p style=\"text-align:center\">Circulate Members</p></html>");
		circulate.setBounds(660,340,300,30);
		circulate.setBackground(new Color(43,68,155));
		circulate.setForeground(Color.WHITE);
		circulate.setEnabled(false);
		circulate.setFont(new Font("Arial", Font.BOLD, 16));
		circulate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				VoiceChannel v = voicechannels.get(guildSelector.getSelectedIndex()).get(channelSelector.getSelectedIndex());

	    		name.setText("");
        		filename.setText("");
        		file = null;
        		filename.setVisible(false);
        		select.setVisible(false);
        		play.setVisible(false);
        		name.setVisible(false);
        		done.setVisible(false);
        		guildSelector.setEnabled(false);
        		channelSelector.setEnabled(false);
        		change.setEnabled(false);
        		circulate.setEnabled(false);
        		clear.setEnabled(false);
        		mute.setEnabled(false);
        		unmute.setEnabled(false);
        		deafen.setEnabled(false);
        		undeafen.setEnabled(false);
        		kick.setEnabled(false);
        		sound.setEnabled(false);
        		
	    		try {
					circulator(v, voicechannels.get(guildSelector.getSelectedIndex()));
				} catch (IOException | LoginException | InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
    
		change = new JButton("<html><p style=\"text-align:center\">Change<br>Nicknames</p></html>");
		change.setBounds(380, 20, 120, 60);
		change.setBackground(new Color(43,68,155));
		change.setForeground(Color.WHITE);
		change.setEnabled(false);
		change.setFont(new Font("Arial", Font.BOLD, 16));
		change.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(filename.isVisible()) {
					filename.setText("");
            		file = null;
            		filename.setVisible(false);
            		select.setVisible(false);
            		play.setVisible(false);
				}
				
				if(!name.isVisible()) {
					name.setText("");
					name.setVisible(true);
					done.setVisible(true);
				} else {
					name.setText("");
					name.setVisible(false);
					done.setVisible(false);
				}
			}
		});
		
		clear = new JButton("<html><p style=\"text-align:center\">Clear<br>Nicknames</p></html>");
	    clear.setBounds(520, 20, 120, 60);
	    clear.setBackground(new Color(43,68,155));
	    clear.setForeground(Color.WHITE);
		clear.setEnabled(false);
	    clear.setFont(new Font("Arial", Font.BOLD, 16));
	    clear.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		VoiceChannel v = voicechannels.get(guildSelector.getSelectedIndex()).get(channelSelector.getSelectedIndex());
	    		
	    		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyy HH:mm:ss");
	    		LocalDateTime now = LocalDateTime.now();
	    		
	    		log.setText("Activity Log:\n\n[" + dtf.format(now) + "]\n");
            
	    		try {
	    			clear(v);
	    		} catch (LoginException|InterruptedException | IOException e1) {
	            	e1.printStackTrace();
	    		}
	    	}
	    });
	    
	    mute = new JButton("<html><p style=\"text-align:center\">Mute<br>Members</p></html>");
	    mute.setBounds(380, 100, 120, 60);
	    mute.setBackground(new Color(43,68,155));
	    mute.setForeground(Color.WHITE);
		mute.setEnabled(false);
	    mute.setFont(new Font("Arial", Font.BOLD, 16));
	    mute.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		VoiceChannel v = voicechannels.get(guildSelector.getSelectedIndex()).get(channelSelector.getSelectedIndex());
	    		
	    		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyy HH:mm:ss");
	    		LocalDateTime now = LocalDateTime.now();
	    		
	    		log.setText("Activity Log:\n\n[" + dtf.format(now) + "]\n");
            
	    		try {
	    			mute(v);
	    		} catch (LoginException|InterruptedException | IOException e1) {
	            	e1.printStackTrace();
	    		}
	    	}
	    });
	    
	    unmute = new JButton("<html><p style=\"text-align:center\">Unmute<br>Members</p></html>");
	    unmute.setBounds(520, 100, 120, 60);
	    unmute.setBackground(new Color(43,68,155));
	    unmute.setForeground(Color.WHITE);
		unmute.setEnabled(false);
	    unmute.setFont(new Font("Arial", Font.BOLD, 16));
	    unmute.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		VoiceChannel v = voicechannels.get(guildSelector.getSelectedIndex()).get(channelSelector.getSelectedIndex());
	    		
	    		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyy HH:mm:ss");
	    		LocalDateTime now = LocalDateTime.now();
	    		
	    		log.setText("Activity Log:\n\n[" + dtf.format(now) + "]\n");
            
	    		try {
	    			unmute(v);
	    		} catch (LoginException|InterruptedException | IOException e1) {
	            	e1.printStackTrace();
	    		}
	    	}
	    });
	    
	    deafen = new JButton("<html><p style=\"text-align:center\">Deafen<br>Members</p></html>");
	    deafen.setBounds(380, 180, 120, 60);
	    deafen.setBackground(new Color(43,68,155));
	    deafen.setForeground(Color.WHITE);
		deafen.setEnabled(false);
	    deafen.setFont(new Font("Arial", Font.BOLD, 16));
	    deafen.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		VoiceChannel v = voicechannels.get(guildSelector.getSelectedIndex()).get(channelSelector.getSelectedIndex());
	    		
	    		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyy HH:mm:ss");
	    		LocalDateTime now = LocalDateTime.now();
	    		
	    		log.setText("Activity Log:\n\n[" + dtf.format(now) + "]\n");
            
	    		try {
	    			deafen(v);
	    		} catch (LoginException|InterruptedException | IOException e1) {
	            	e1.printStackTrace();
	    		}
	    	}
	    });
	    
	    undeafen = new JButton("<html><p style=\"text-align:center\">Undeafen<br>Members</p></html>");
	    undeafen.setBounds(520, 180, 120, 60);
	    undeafen.setBackground(new Color(43,68,155));
	    undeafen.setForeground(Color.WHITE);
		undeafen.setEnabled(false);
	    undeafen.setFont(new Font("Arial", Font.BOLD, 16));
	    undeafen.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		VoiceChannel v = voicechannels.get(guildSelector.getSelectedIndex()).get(channelSelector.getSelectedIndex());
	    		
	    		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyy HH:mm:ss");
	    		LocalDateTime now = LocalDateTime.now();
	    		
	    		log.setText("Activity Log:\n\n[" + dtf.format(now) + "]\n");
            
	    		try {
	    			undeafen(v);
	    		} catch (LoginException|InterruptedException | IOException e1) {
	            	e1.printStackTrace();
	    		}
	    	}
	    });
	    
	    kick = new JButton("<html><p style=\"text-align:center\">Kick<br>Members</p></html>");
	    kick.setBounds(380, 260, 120, 60);
	    kick.setBackground(new Color(43,68,155));
	    kick.setForeground(Color.WHITE);
		kick.setEnabled(false);
	    kick.setFont(new Font("Arial", Font.BOLD, 16));
	    kick.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		VoiceChannel v = voicechannels.get(guildSelector.getSelectedIndex()).get(channelSelector.getSelectedIndex());
	    		
	    		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyy HH:mm:ss");
	    		LocalDateTime now = LocalDateTime.now();
	    		
	    		log.setText("Activity Log:\n\n[" + dtf.format(now) + "]\n");
            
	    		try {
	    			kick(v);
	    		} catch (LoginException | InterruptedException | IOException e1) {
	            	e1.printStackTrace();
	    		}
	    	}
	    });
    
	    sound = new JButton("<html><p style=\"text-align:center\">Play<br>Sound</p></html>");
	    sound.setBounds(520, 260, 120, 60);
	    sound.setBackground(new Color(43,68,155));
	    sound.setForeground(Color.WHITE);
	    sound.setEnabled(false);
	    sound.setFont(new Font("Arial", Font.BOLD, 16));
	    sound.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		
	    		if(name.isVisible()) {
	    			name.setText("");
		    		name.setVisible(false);
		    		done.setVisible(false);
	    		}
	    		
	    		if(!filename.isVisible()) {
	    			filename.setText("");
        			file = null;
        			filename.setVisible(true);
        			select.setVisible(true);
        			play.setVisible(true);
	    		} else {
	    			filename.setText("");
            		file = null;
            		filename.setVisible(false);
            		select.setVisible(false);
            		play.setVisible(false);
	    		}
	    	}
	    });
	    
	    fileChooser = new JFileChooser();
	    FileNameExtensionFilter filter = new FileNameExtensionFilter(
	            "Audio Files", "mp3", "flac", "wav", "m4a", "aac", "ogg");
	    fileChooser.setFileFilter(filter);
	    fileChooser.setAcceptAllFileFilterUsed(false);
	    fileChooser.setDialogTitle("Select an audio file to play.");
	    fileChooser.setCurrentDirectory(new File("C:\\Users\\egeka\\Desktop\\sounds"));
	    
	    filename = new JTextField();
		filename.setBounds(380, 340, 140, 30);
		filename.setVisible(false);
		filename.setEditable(false);
		filename.setBackground(new Color(43,68,155));
		filename.setForeground(Color.WHITE);
		filename.setFont(new Font("Arial", Font.PLAIN, 11));
		filename.setHorizontalAlignment(JTextField.CENTER);
		
		
		select = new JButton("File");
	    select.setBounds(520, 340, 60, 30);
	    select.setVisible(false);
	    select.setBackground(new Color(43,68,155));
	    select.setForeground(Color.WHITE);
	    select.setFont(new Font("Arial", Font.BOLD, 11));
	    select.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		int returnVal = fileChooser.showOpenDialog(f);
	    		
	    		if(returnVal == JFileChooser.APPROVE_OPTION) {
	    			file = fileChooser.getSelectedFile();
	    			
	    			filename.setText(file.getName());
	    		}
	    	}
	    });
	    
	    
	    play = new JButton("Play");
	    play.setBounds(580, 340, 60, 30);
	    play.setVisible(false);
	    play.setBackground(new Color(43,68,155));
	    play.setForeground(Color.WHITE);
	    play.setFont(new Font("Arial", Font.BOLD, 11));
	    play.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		VoiceChannel v = voicechannels.get(guildSelector.getSelectedIndex()).get(channelSelector.getSelectedIndex());
				
	    		if(file != null && file.canRead()) {
	    			System.out.println(file.canRead());
		    		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyy HH:mm:ss");
		    		LocalDateTime now = LocalDateTime.now();
		    		
		    		log.setText("Activity Log:\n\n[" + dtf.format(now) + "]\n");
		    		
		    		channelSelector.setEnabled(false);
		    		guildSelector.setEnabled(false);
		    		change.setEnabled(false);
		    		change.setText("<html><p style=\"text-align:center\">Wait</p></html>");
		    		circulate.setEnabled(false);
		    		circulate.setText("<html><p style=\"text-align:center\">Wait</p></html>");
		    		clear.setEnabled(false);
		    		clear.setText("<html><p style=\"text-align:center\">Wait</p></html>");
		    		mute.setEnabled(false);
		    		mute.setText("<html><p style=\"text-align:center\">Wait</p></html>");
		    		unmute.setEnabled(false);
		    		unmute.setText("<html><p style=\"text-align:center\">Wait</p></html>");
		    		deafen.setEnabled(false);
		    		deafen.setText("<html><p style=\"text-align:center\">Wait</p></html>");
		    		undeafen.setEnabled(false);
		    		undeafen.setText("<html><p style=\"text-align:center\">Wait</p></html>");
		    		kick.setEnabled(false);
		    		kick.setText("<html><p style=\"text-align:center\">Wait</p></html>");
		    		sound.setEnabled(false);
		    		sound.setText("<html><p style=\"text-align:center\">Wait</p></html>");
		    		filename.setEnabled(false);
		    		select.setEnabled(false);
		    		play.setEnabled(false);
	            
		    		try {
		    			loadAndPlay(v, file.getAbsolutePath());
		    		} catch (LoginException|InterruptedException | IOException e1) {
		            	e1.printStackTrace();
		    		}
	    		}
	    	}
	    });
    
	    done = new JButton("OK");
	    done.setBounds(590, 340, 50, 30);
	    done.setVisible(false);
	    done.setBackground(new Color(43,68,155));
	    done.setForeground(Color.WHITE);
	    done.setFont(new Font("Arial", Font.BOLD, 11));
	    done.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		String n = name.getText().trim();
	    		
	    		VoiceChannel v = voicechannels.get(guildSelector.getSelectedIndex()).get(channelSelector.getSelectedIndex());
            
	    		if (n.length() >= 1) {
	    			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyy HH:mm:ss");
		    		LocalDateTime now = LocalDateTime.now();
		    		
		    		log.setText("Activity Log:\n\n[" + dtf.format(now) + "]\n");
              
	    			try {
	    				change(n, v);
	    			} catch (LoginException|InterruptedException | IOException e1) {
	    				e1.printStackTrace();
	    			} 
	    		}
	    	}
	    });
	    
	    JLabel invite = new JLabel("Invite Bot");
	    invite.setBounds(20,375,100,11);
	    invite.setForeground(new Color(43,68,155));
	    invite.setFont(new Font("Arial", Font.BOLD, 11));
	    invite.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					Desktop.getDesktop().browse(new URI("https://discord.com/api/oauth2/authorize?client_id=744005136237723729&permissions=8&scope=bot"));
				} catch (IOException | URISyntaxException e1) {
					e1.printStackTrace();
				}
			}
		});
	    
	    initial5Sec();
	    
	    p.add(change);
	    p.add(clear);
	    p.add(sound);
	    p.add(name);
	    p.add(done);
	    p.add(logScr);
	    p.add(guildScr);
	    p.add(channelScr);
	    p.add(mute);
	    p.add(unmute);
	    p.add(deafen);
	    p.add(circulate);
	    p.add(undeafen);
	    p.add(kick);
	    p.add(select);
	    p.add(filename);
	    p.add(play);
	    p.add(invite);
	    p.setBackground(new Color(114,137,217));
    
	    p.revalidate();
	    p.repaint();
	}
	
	public static JDA jda = null;
	public static List<Guild> guilds = new ArrayList<Guild>();
	public static List<List<VoiceChannel>> voicechannels = new ArrayList<List<VoiceChannel>>();
	
	public static DefaultListModel<String> getGuildNames() throws LoginException, InterruptedException {
		jda = JDABuilder.createDefault(DISCORD_TOKEN).setAutoReconnect(true).build().awaitReady();
		f.setTitle("Channel Troller Control Panel (No Channel Selected)");
		
		guilds = jda.getGuilds();
		
		jda.shutdownNow();
		OkHttpClient client = jda.getHttpClient();
		client.connectionPool().evictAll();
		client.dispatcher().executorService().shutdown();
		jda = null;
		
		DefaultListModel<String> list = new DefaultListModel<String>();
		for(Guild g : guilds) {
			voicechannels.add(g.getVoiceChannels());
			list.addElement(g.getName());
		}
		
		return list;
	}
	
	public static DefaultListModel<String> getChannelNames(int index){
		DefaultListModel<String> list = new DefaultListModel<String>();
		
		for(VoiceChannel v : voicechannels.get(index)) {
			list.addElement(v.getName());
		}
		
		return list;
	}
	
	private static AudioPlayerManager playerManager;
	private static Map<Long, GuildMusicManager> musicManagers;

	private Bot() {
		Bot.musicManagers = new HashMap<>();

		Bot.playerManager = new DefaultAudioPlayerManager();
		AudioSourceManagers.registerRemoteSources(playerManager);
		AudioSourceManagers.registerLocalSource(playerManager);
	}
	
	private synchronized static GuildMusicManager getGuildAudioPlayer(Guild guild) {

		long guildId = Long.parseLong(guild.getId());
	    
		GuildMusicManager musicManager = musicManagers.get(guildId);
		if (musicManager == null) {
			musicManager = new GuildMusicManager(playerManager);
			musicManagers.put(guildId, musicManager);
			
		}

		guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());
	    
		return musicManager;
	}
	
	public static Guild g = null;
	public static VoiceChannel c = null;
	
	public static void closeAudio() {
		Timer timer = new Timer();
		
		TimerTask task = new TimerTask() {
			public void run() {
				closeBot();
			}
		};
		
		timer.schedule(task, 1);
	}
	
	public static void initial5Sec() {
		Timer timer = new Timer();
		
		change.setText("<html><p style=\"text-align:center\">5</p></html>");
		circulate.setText("<html><p style=\"text-align:center\">5</p></html>");
		clear.setText("<html><p style=\"text-align:center\">5</p></html>");
		mute.setText("<html><p style=\"text-align:center\">5</p></html>");
		unmute.setText("<html><p style=\"text-align:center\">5</p></html>");
		deafen.setText("<html><p style=\"text-align:center\">5</p></html>");
		undeafen.setText("<html><p style=\"text-align:center\">5</p></html>");
		kick.setText("<html><p style=\"text-align:center\">5</p></html>");
		sound.setText("<html><p style=\"text-align:center\">5</p></html>");
		
		TimerTask four = new TimerTask() {
			public void run() {
				change.setText("<html><p style=\"text-align:center\">4</p></html>");
				circulate.setText("<html><p style=\"text-align:center\">4</p></html>");
				clear.setText("<html><p style=\"text-align:center\">4</p></html>");
				mute.setText("<html><p style=\"text-align:center\">4</p></html>");
				unmute.setText("<html><p style=\"text-align:center\">4</p></html>");
				deafen.setText("<html><p style=\"text-align:center\">4</p></html>");
				undeafen.setText("<html><p style=\"text-align:center\">4</p></html>");
				kick.setText("<html><p style=\"text-align:center\">4</p></html>");
				sound.setText("<html><p style=\"text-align:center\">4</p></html>");
			}
		};
		
		TimerTask three = new TimerTask() {
			public void run() {
				change.setText("<html><p style=\"text-align:center\">3</p></html>");
				circulate.setText("<html><p style=\"text-align:center\">3</p></html>");
				clear.setText("<html><p style=\"text-align:center\">3</p></html>");
				mute.setText("<html><p style=\"text-align:center\">3</p></html>");
				unmute.setText("<html><p style=\"text-align:center\">3</p></html>");
				deafen.setText("<html><p style=\"text-align:center\">3</p></html>");
				undeafen.setText("<html><p style=\"text-align:center\">3</p></html>");
				kick.setText("<html><p style=\"text-align:center\">3</p></html>");
				sound.setText("<html><p style=\"text-align:center\">3</p></html>");
			}
		};
		
		TimerTask two = new TimerTask() {
			public void run() {
				change.setText("<html><p style=\"text-align:center\">2</p></html>");
				circulate.setText("<html><p style=\"text-align:center\">2</p></html>");
				clear.setText("<html><p style=\"text-align:center\">2</p></html>");
				mute.setText("<html><p style=\"text-align:center\">2</p></html>");
				unmute.setText("<html><p style=\"text-align:center\">2</p></html>");
				deafen.setText("<html><p style=\"text-align:center\">2</p></html>");
				undeafen.setText("<html><p style=\"text-align:center\">2</p></html>");
				kick.setText("<html><p style=\"text-align:center\">2</p></html>");
				sound.setText("<html><p style=\"text-align:center\">2</p></html>");
			}
		};
		
		TimerTask one = new TimerTask() {
			public void run() {
				change.setText("<html><p style=\"text-align:center\">1</p></html>");
				circulate.setText("<html><p style=\"text-align:center\">1</p></html>");
				clear.setText("<html><p style=\"text-align:center\">1</p></html>");
				mute.setText("<html><p style=\"text-align:center\">1</p></html>");
				unmute.setText("<html><p style=\"text-align:center\">1</p></html>");
				deafen.setText("<html><p style=\"text-align:center\">1</p></html>");
				undeafen.setText("<html><p style=\"text-align:center\">1</p></html>");
				kick.setText("<html><p style=\"text-align:center\">1</p></html>");
				sound.setText("<html><p style=\"text-align:center\">1</p></html>");
			}
		};
		
		TimerTask zero = new TimerTask() {
			public void run() {
				change.setText("<html><p style=\"text-align:center\">Change<br>Nicknames</p></html>");
				circulate.setText("<html><p style=\"text-align:center\">Circulate Members</p></html>");
				clear.setText("<html><p style=\"text-align:center\">Clear<br>Nicknames</p></html>");
				mute.setText("<html><p style=\"text-align:center\">Mute<br>Members</p></html>");
				unmute.setText("<html><p style=\"text-align:center\">Unmute<br>Members</p></html>");
				deafen.setText("<html><p style=\"text-align:center\">Deafen<br>Members</p></html>");
				undeafen.setText("<html><p style=\"text-align:center\">Undeafen<br>Members</p></html>");
				kick.setText("<html><p style=\"text-align:center\">Kick<br>Members</p></html>");
				sound.setText("<html><p style=\"text-align:center\">Play<br>Sound</p></html>");
				guildSelector.setEnabled(true);
			}
		};
		
		timer.schedule(four, 1000);
		timer.schedule(three, 2000);
		timer.schedule(two, 3000);
		timer.schedule(one, 4000);
		timer.schedule(zero, 5000);
	}
	
	public static void closeBot() {
		channelSelector.setEnabled(false);
		guildSelector.setEnabled(false);
		change.setEnabled(false);
		change.setText("<html><p style=\"text-align:center\">5</p></html>");
		circulate.setEnabled(false);
		circulate.setText("<html><p style=\"text-align:center\">5</p></html>");
		clear.setEnabled(false);
		clear.setText("<html><p style=\"text-align:center\">5</p></html>");
		name.setText("");
		name.setEnabled(false);
		name.setVisible(false);
		done.setEnabled(false);
		done.setVisible(false);
		mute.setEnabled(false);
		mute.setText("<html><p style=\"text-align:center\">5</p></html>");
		unmute.setEnabled(false);
		unmute.setText("<html><p style=\"text-align:center\">5</p></html>");
		deafen.setEnabled(false);
		deafen.setText("<html><p style=\"text-align:center\">5</p></html>");
		undeafen.setEnabled(false);
		undeafen.setText("<html><p style=\"text-align:center\">5</p></html>");
		kick.setEnabled(false);
		kick.setText("<html><p style=\"text-align:center\">5</p></html>");
		sound.setEnabled(false);
		sound.setText("<html><p style=\"text-align:center\">5</p></html>");
		filename.setText("");
		filename.setEnabled(false);
		filename.setVisible(false);
		select.setEnabled(false);
		select.setVisible(false);
		play.setEnabled(false);
		play.setVisible(false);
		file = null;
		c = null;
		g = null;
		
		jda.shutdownNow();
		OkHttpClient client = jda.getHttpClient();
		client.connectionPool().evictAll();
		client.dispatcher().executorService().shutdown();
		jda = null;
		
		Timer timer = new Timer();
		
		TimerTask four = new TimerTask() {
			public void run() {
				change.setText("<html><p style=\"text-align:center\">4</p></html>");
				circulate.setText("<html><p style=\"text-align:center\">4</p></html>");
				clear.setText("<html><p style=\"text-align:center\">4</p></html>");
				mute.setText("<html><p style=\"text-align:center\">4</p></html>");
				unmute.setText("<html><p style=\"text-align:center\">4</p></html>");
				deafen.setText("<html><p style=\"text-align:center\">4</p></html>");
				undeafen.setText("<html><p style=\"text-align:center\">4</p></html>");
				kick.setText("<html><p style=\"text-align:center\">4</p></html>");
				sound.setText("<html><p style=\"text-align:center\">4</p></html>");
			}
		};
		
		TimerTask three = new TimerTask() {
			public void run() {
				change.setText("<html><p style=\"text-align:center\">3</p></html>");
				circulate.setText("<html><p style=\"text-align:center\">3</p></html>");
				clear.setText("<html><p style=\"text-align:center\">3</p></html>");
				mute.setText("<html><p style=\"text-align:center\">3</p></html>");
				unmute.setText("<html><p style=\"text-align:center\">3</p></html>");
				deafen.setText("<html><p style=\"text-align:center\">3</p></html>");
				undeafen.setText("<html><p style=\"text-align:center\">3</p></html>");
				kick.setText("<html><p style=\"text-align:center\">3</p></html>");
				sound.setText("<html><p style=\"text-align:center\">3</p></html>");
			}
		};
		
		TimerTask two = new TimerTask() {
			public void run() {
				change.setText("<html><p style=\"text-align:center\">2</p></html>");
				circulate.setText("<html><p style=\"text-align:center\">2</p></html>");
				clear.setText("<html><p style=\"text-align:center\">2</p></html>");
				mute.setText("<html><p style=\"text-align:center\">2</p></html>");
				unmute.setText("<html><p style=\"text-align:center\">2</p></html>");
				deafen.setText("<html><p style=\"text-align:center\">2</p></html>");
				undeafen.setText("<html><p style=\"text-align:center\">2</p></html>");
				kick.setText("<html><p style=\"text-align:center\">2</p></html>");
				sound.setText("<html><p style=\"text-align:center\">2</p></html>");
			}
		};
		
		TimerTask one = new TimerTask() {
			public void run() {
				change.setText("<html><p style=\"text-align:center\">1</p></html>");
				circulate.setText("<html><p style=\"text-align:center\">1</p></html>");
				clear.setText("<html><p style=\"text-align:center\">1</p></html>");
				mute.setText("<html><p style=\"text-align:center\">1</p></html>");
				unmute.setText("<html><p style=\"text-align:center\">1</p></html>");
				deafen.setText("<html><p style=\"text-align:center\">1</p></html>");
				undeafen.setText("<html><p style=\"text-align:center\">1</p></html>");
				kick.setText("<html><p style=\"text-align:center\">1</p></html>");
				sound.setText("<html><p style=\"text-align:center\">1</p></html>");
			}
		};
		
		TimerTask zero = new TimerTask() {
			public void run() {
				change.setText("<html><p style=\"text-align:center\">Change<br>Nicknames</p></html>");
				circulate.setText("<html><p style=\"text-align:center\">Circulate Members</p></html>");
				clear.setText("<html><p style=\"text-align:center\">Clear<br>Nicknames</p></html>");
				mute.setText("<html><p style=\"text-align:center\">Mute<br>Members</p></html>");
				unmute.setText("<html><p style=\"text-align:center\">Unmute<br>Members</p></html>");
				deafen.setText("<html><p style=\"text-align:center\">Deafen<br>Members</p></html>");
				undeafen.setText("<html><p style=\"text-align:center\">Undeafen<br>Members</p></html>");
				kick.setText("<html><p style=\"text-align:center\">Kick<br>Members</p></html>");
				sound.setText("<html><p style=\"text-align:center\">Play<br>Sound</p></html>");
				channelSelector.setEnabled(true);
				guildSelector.setEnabled(true);
				change.setEnabled(true);
				circulate.setEnabled(true);
				name.setEnabled(true);
				done.setEnabled(true);
				clear.setEnabled(true);
				mute.setEnabled(true);
				unmute.setEnabled(true);
				deafen.setEnabled(true);
				undeafen.setEnabled(true);
				kick.setEnabled(true);
				sound.setEnabled(true);
				filename.setEnabled(true);
				select.setEnabled(true);
				play.setEnabled(true);
				
				timer.cancel();
			}
		};
		
		timer.schedule(four, 1000);
		timer.schedule(three, 2000);
		timer.schedule(two, 3000);
		timer.schedule(one, 4000);
		timer.schedule(zero, 5000);
	}
	
	private static void loadAndPlay(VoiceChannel channel, final String trackUrl) throws LoginException, InterruptedException, IOException {
		jda = JDABuilder.createDefault(DISCORD_TOKEN).addEventListeners(new Bot()).setAutoReconnect(true).build().awaitReady();  
	    
		c = jda.getVoiceChannelById(channel.getId());
		g = c.getGuild();
		List<Member> members = c.getMembers();
		updateBlacklist();
		  
		if (c.getGuild().getAfkChannel() != null && c.getGuild().getAfkChannel().equals(c)) {
			log.setText(log.getText() + "\nPlay Sound\n-----------------------\nTarget Guild: " + c.getGuild().getName() + "\nTarget Channel: " + c.getName()
			+ "\n\nYou cannot perform actions on an AFK channel. Terminating...");
			closeBot();
			return;
		} else if(!(members.size() >= 1)) {
			log.setText(log.getText() + "\nPlay Sound\n-----------------------\nTarget Guild: " + c.getGuild().getName() + "\nTarget Channel: " + c.getName()
			+ "\n\nThere is no one on the voice channel. Terminating...");
			closeBot();
			return;
		} 
		
		log.setText(log.getText() + "\nPlay Sound\n-----------------------\nTarget Guild: " + c.getGuild().getName() + "\nTarget Channel: " + c.getName()
		+ "\n\nPlaying sound \"" + file.getName() + "\"");
		
	    GuildMusicManager musicManager = getGuildAudioPlayer(c.getGuild());

	    playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
	      @Override
	      public void trackLoaded(AudioTrack track) {
	        play(c.getGuild(), musicManager, track, c);
	      }

	      @Override
	      public void playlistLoaded(AudioPlaylist playlist) {
	    	  
	      }

	      @Override
	      public void noMatches() {
	        
	      }

	      @Override
	      public void loadFailed(FriendlyException exception) {
	        
	      }
	    });
	  }
	
	@Override
	public void onGuildVoiceLeave(@Nonnull GuildVoiceLeaveEvent event) {
		if(event.getMember().equals(event.getGuild().getSelfMember())) {
			try {
				TimeUnit.MILLISECONDS.sleep(135);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			event.getGuild().getAudioManager().openAudioConnection(c);
		}
	}
	
	public void onGuildVoiceMove(@Nonnull GuildVoiceMoveEvent event) {
		if(event.getMember().equals(event.getGuild().getSelfMember())) {
			try {
				TimeUnit.MILLISECONDS.sleep(135);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			event.getGuild().getAudioManager().openAudioConnection(c);
		} else if(event.getChannelLeft().equals(c) && !blacklist.contains(event.getMember().getId())) {
			try {
				TimeUnit.MILLISECONDS.sleep(135);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			event.getGuild().moveVoiceMember(event.getMember(), c).complete();
		}
	}
	
	@Override
	public void onGuildVoiceGuildMute(@Nonnull GuildVoiceGuildMuteEvent event) {
		if(event.getMember().equals(event.getGuild().getSelfMember())) {
			if(event.getGuild().getSelfMember().getVoiceState().isGuildMuted()) {
				event.getGuild().getSelfMember().mute(false).complete();
			}
		}
	}
	
	@Override
	public void onGuildVoiceGuildDeafen(@Nonnull GuildVoiceGuildDeafenEvent event) {
		if(event.getMember().equals(event.getGuild().getSelfMember())) {
			if(event.getGuild().getSelfMember().getVoiceState().isGuildDeafened()) {
				event.getGuild().getSelfMember().deafen(false).complete();
			}
		}
	}

	private static void play(Guild guild, GuildMusicManager musicManager, AudioTrack track, VoiceChannel channel) {
	    connectToFirstVoiceChannel(channel, guild.getAudioManager());

	    musicManager.scheduler.queue(track);
	  }
	  
	  private static void connectToFirstVoiceChannel(VoiceChannel channel, AudioManager audioManager) {
	    if (!audioManager.isConnected()) {
	        audioManager.openAudioConnection(channel);
	    }
	  }
	
	public static void mute(VoiceChannel channel) throws LoginException, InterruptedException, IOException {
		  jda = JDABuilder.createDefault(DISCORD_TOKEN).setAutoReconnect(true).build().awaitReady();  
		    
		  c = jda.getVoiceChannelById(channel.getId());
		  List<Member> members = c.getMembers();
		  updateBlacklist();
		  
		  if(c.getGuild().getAfkChannel() != null && c.getGuild().getAfkChannel().equals(c)) {
			  log.setText(log.getText() + "\nMute Members\n-----------------------\nTarget Guild: " + c.getGuild().getName() + "\nTarget Channel: " + c.getName()
				+ "\n\nYou cannot perform actions on an AFK channel. Terminating...");
				closeBot();
				return;
		  } else if(!(members.size() >= 1)) {
				log.setText(log.getText() + "\nMute Members\n-----------------------\nTarget Guild: " + c.getGuild().getName() + "\nTarget Channel: " + c.getName()
				+ "\n\nThere is no one on the voice channel. Terminating...");
				closeBot();
				return;
		  } 
		  
		  log.setText(log.getText() + "\nMute Members\n-----------------------\nTarget Guild: " + c.getGuild().getName() +
				  "\nTarget Channel: " + c.getName() + "\n\nMembers:\n-----------------------");
		  for (Member m : members) {
			  log.setText(log.getText() + "\n" + m.getUser().getName() + " : ");
			  
			  if(!blacklist.contains(m.getId()))
			  	m.mute(true).complete();
			  
			  if(blacklist.contains(m.getId()))
				  log.setText(log.getText() + "Blacklisted.");
			  else if(m.getVoiceState().isGuildMuted())
				  log.setText(log.getText() + "Done!");
			  else
				  log.setText(log.getText() + "Failed.");
		  }
	    
		  closeBot();
	}
	
	public static void unmute(VoiceChannel channel) throws LoginException, InterruptedException, IOException {
		  jda = JDABuilder.createDefault(DISCORD_TOKEN).setAutoReconnect(true).build().awaitReady();  
		    
		  c = jda.getVoiceChannelById(channel.getId());
		  List<Member> members = c.getMembers();
		  updateBlacklist();
		  
		  if(c.getGuild().getAfkChannel() != null && c.getGuild().getAfkChannel().equals(c)) {
			  log.setText(log.getText() + "\nUnmute Members\n-----------------------\nTarget Guild: " + c.getGuild().getName() + "\nTarget Channel: " + c.getName()
				+ "\n\nYou cannot perform actions on an AFK channel. Terminating...");
				closeBot();
				return;
		  } else if(!(members.size() >= 1)) {
				log.setText(log.getText() + "\nUnmute Members\n-----------------------\nTarget Guild: " + c.getGuild().getName() + "\nTarget Channel: " + c.getName()
				+ "\n\nThere is no one on the voice channel. Terminating...");
				closeBot();
				return;
		  }
		  
		  log.setText(log.getText() + "\nUnmute Members\n-----------------------\nTarget Guild: " + c.getGuild().getName() + "\nTarget Channel: " + c.getName()
		  + "\n\nMembers:\n-----------------------");
	    
		  for (Member m : members) {
			  log.setText(log.getText() + "\n" + m.getUser().getName() + " : ");
			  
			  if(!blacklist.contains(m.getId()))
				  	m.mute(false).complete();
			  
			  if(blacklist.contains(m.getId()))
				  log.setText(log.getText() + "Blacklisted.");
			  else if(!m.getVoiceState().isGuildMuted())
				  log.setText(log.getText() + "Done!");
			  else
				  log.setText(log.getText() + "Failed.");
		  }
	    
		  closeBot();
	}
	
	public static void deafen(VoiceChannel channel) throws LoginException, InterruptedException, IOException {
		  jda = JDABuilder.createDefault(DISCORD_TOKEN).setAutoReconnect(true).build().awaitReady();  
		    
		  c = jda.getVoiceChannelById(channel.getId());
		  List<Member> members = c.getMembers();
		  updateBlacklist();
		  
		  if(c.getGuild().getAfkChannel() != null && c.getGuild().getAfkChannel().equals(c)) {
			  log.setText(log.getText() + "\nDeafen Members\n-----------------------\nTarget Guild: " + c.getGuild().getName() + "\nTarget Channel: " + c.getName()
				+ "\n\nYou cannot perform actions on an AFK channel. Terminating...");
				closeBot();
				return;
		  } else if(!(members.size() >= 1)) {
				log.setText(log.getText() + "\nDeafen Members\n-----------------------\nTarget Guild: " + c.getGuild().getName() + "\nTarget Channel: " + c.getName()
				+ "\n\nThere is no one on the voice channel. Terminating...");
				closeBot();
				return;
		  }
		  
		  log.setText(log.getText() + "\nDeafen Members\n-----------------------\nTarget Guild: " + c.getGuild().getName() +
				  "\nTarget Channel: " + c.getName() + "\n\nMembers:\n-----------------------");
	    
		  for (Member m : members) {
			  log.setText(log.getText() + "\n" + m.getUser().getName() + " : ");
			  
			  if(!blacklist.contains(m.getId()))
				  	m.deafen(true).complete();
			  
			  if(blacklist.contains(m.getId()))
				  log.setText(log.getText() + "Blacklisted.");
			  else if(m.getVoiceState().isGuildDeafened())
				  log.setText(log.getText() + "Done!");
			  else
				  log.setText(log.getText() + "Failed.");
		  }
	    
		  closeBot();
	}
	
	public static void undeafen(VoiceChannel channel) throws LoginException, InterruptedException, IOException {
		  jda = JDABuilder.createDefault(DISCORD_TOKEN).setAutoReconnect(true).build().awaitReady();  
		    
		  c = jda.getVoiceChannelById(channel.getId());
		  List<Member> members = c.getMembers();
		  updateBlacklist();
		  
		  if(c.getGuild().getAfkChannel() != null && c.getGuild().getAfkChannel().equals(c)) {
			  log.setText(log.getText() + "\nUndeafen Members\n-----------------------\nTarget Guild: " + c.getGuild().getName() + "\nTarget Channel: " + c.getName()
				+ "\n\nYou cannot perform actions on an AFK channel. Terminating...");
				closeBot();
				return;
		  } else if(!(members.size() >= 1)) {
				log.setText(log.getText() + "\nUndeafen Members\n-----------------------\nTarget Guild: " + c.getGuild().getName() + "\nTarget Channel: " + c.getName()
				+ "\n\nThere is no one on the voice channel. Terminating...");
				closeBot();
				return;
		  }
		  
		  log.setText(log.getText() + "\nUndeafen Members\n-----------------------\nTarget Guild: " + c.getGuild().getName()
				  + "\nTarget Channel: " + c.getName() + "\n\nMembers:\n-----------------------");
	    
		  for (Member m : members) {
			  log.setText(log.getText() + "\n" + m.getUser().getName() + " : ");
			  
			  if(!blacklist.contains(m.getId()))
				  	m.deafen(false).complete();
			  
			  if(blacklist.contains(m.getId()))
				  log.setText(log.getText() + "Blacklisted.");
			  else if(!m.getVoiceState().isGuildDeafened())
				  log.setText(log.getText() + "Done!");
			  else
				  log.setText(log.getText() + "Failed.");
		  }
	    
		  closeBot();
	}
	
	public static void kick(VoiceChannel channel) throws LoginException, InterruptedException, IOException {
		  jda = JDABuilder.createDefault(DISCORD_TOKEN).setAutoReconnect(true).build().awaitReady();  
		    
		  c = jda.getVoiceChannelById(channel.getId());
		  List<Member> members = c.getMembers();
		  updateBlacklist();
		  
		  if(c.getGuild().getAfkChannel() != null && c.getGuild().getAfkChannel().equals(c)) {
			  log.setText(log.getText() + "\nKick Members\n-----------------------\nTarget Guild: " + c.getGuild().getName() + "\nTarget Channel: " + c.getName()
				+ "\n\nYou cannot perform actions on an AFK channel. Terminating...");
				closeBot();
				return;
		  } else if(!(members.size() >= 1)) {
				log.setText(log.getText() + "\nKick Members\n-----------------------\nTarget Guild: " + c.getGuild().getName() + "\nTarget Channel: " + c.getName()
				+ "\n\nThere is no one on the voice channel. Terminating...");
				closeBot();
				return;
		  }
		  
		  log.setText(log.getText() + "\nKick Members\n-----------------------\nTarget Guild: " + c.getGuild().getName() + "\nTarget Channel: " + c.getName()
		  + "\n\nMembers:\n-----------------------");
	    
		  for (Member m : members) {
			  log.setText(log.getText() + "\n" + m.getUser().getName() + " : ");
			  
			  if(!blacklist.contains(m.getId()))
				  c.getGuild().kickVoiceMember(m).complete();
			  
			  if(blacklist.contains(m.getId()))
				  log.setText(log.getText() + "Blacklisted.");
			  else if(!m.getVoiceState().inVoiceChannel())
				  log.setText(log.getText() + "Done!");
			  else
				  log.setText(log.getText() + "Failed.");
		  }
	    
		  closeBot();
	}
  
	public static void change(String name, VoiceChannel channel) throws LoginException, InterruptedException, IOException {
		jda = JDABuilder.createDefault(DISCORD_TOKEN).setAutoReconnect(true).build().awaitReady();
		
		c = jda.getVoiceChannelById(channel.getId());
		List<Member> members = c.getMembers();
		updateBlacklist();
		
		if(c.getGuild().getAfkChannel() != null && c.getGuild().getAfkChannel().equals(c)) {
			  	log.setText(log.getText() + "\nChange Nicknames\n-----------------------\nTarget Name: " + name + "\nTarget Guild: " + c.getGuild().getName() + "\nTarget Channel: " + c.getName()
				+ "\n\nYou cannot perform actions on an AFK channel. Terminating...");
				closeBot();
				return;
		} else if(!(members.size() >= 1)) {
				log.setText(log.getText() + "\nChange Nicknames\n-----------------------\nTarget Name: " + name + "\nTarget Guild: " + c.getGuild().getName() + "\nTarget Channel: " + c.getName()
				+ "\n\nThere is no one on the voice channel. Terminating...");
				closeBot();
				return;
		}
		
		log.setText(log.getText() + "\nChange Nicknames\n-----------------------\nTarget Name: " + name + "\nTarget Guild: " + c.getGuild().getName() + "\nTarget Channel: " + c.getName()
		+ "\n\nMembers:\n-----------------------");
		
		for (Member m : members) {
			log.setText(log.getText() + "\n" + m.getUser().getName() + " : ");
			
			if (channel.getGuild().getSelfMember().canInteract(m) && !blacklist.contains(m.getId())) {
				m.modifyNickname(name).complete();
				log.setText(log.getText() + "Done!");
			} else if(blacklist.contains(m.getId()))
				  log.setText(log.getText() + "Blacklisted.");
			  else
				  log.setText(log.getText() + "Failed.");
		}
    
		closeBot();
	}
  
	public static void clear(VoiceChannel channel) throws LoginException, InterruptedException, IOException {
		jda = JDABuilder.createDefault(DISCORD_TOKEN).setAutoReconnect(true).build().awaitReady();  
	    
		c = jda.getVoiceChannelById(channel.getId());
		List<Member> members = c.getMembers();
		updateBlacklist();
		  
		if(c.getGuild().getAfkChannel() != null && c.getGuild().getAfkChannel().equals(c)) {
			log.setText(log.getText() + "\nClear Nicknames\n-----------------------\nTarget Guild: " + c.getGuild().getName() + "\nTarget Channel: " + c.getName()
				+ "\n\nYou cannot perform actions on an AFK channel. Terminating...");
			closeBot();
			return;
		} else if(!(members.size() >= 1)) {
			log.setText(log.getText() + "\nClear Nicknames\n-----------------------\nTarget Guild: " + c.getGuild().getName() + "\nTarget Channel: " + c.getName()
				+ "\n\nThere is no one on the voice channel. Terminating...");
			closeBot();
			return;
		}
		  
		log.setText(log.getText() + "\nClear Nicknames\n-----------------------\nTarget Guild: " + c.getGuild().getName() + "\nTarget Channel: "
				+ c.getName() + "\n\nMembers:\n-----------------------");
	    
		for (Member m : members) {
			log.setText(log.getText() + "\n" + m.getUser().getName() + " : ");
			  
			if (channel.getGuild().getSelfMember().canInteract(m) && !blacklist.contains(m.getId())) {
				m.modifyNickname(null).complete();
				log.setText(log.getText() + "Done!");
			} else if(blacklist.contains(m.getId()))
				log.setText(log.getText() + "Blacklisted.");
			else
				log.setText(log.getText() + "Failed.");
		}
	    
		closeBot();
	}
	
	@SuppressWarnings("rawtypes")
	public static JList memberSelector = null;
	public static JButton circulateN = null;
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void circulator(VoiceChannel v, List<VoiceChannel> channels) throws IOException, LoginException, InterruptedException {
		jda = JDABuilder.createDefault(DISCORD_TOKEN).setAutoReconnect(true).build().awaitReady();
		
		c = jda.getVoiceChannelById(v.getId());
		List<Member> members = c.getMembers();
		
		jda.shutdownNow();
		OkHttpClient client = jda.getHttpClient();
		client.connectionPool().evictAll();
		client.dispatcher().executorService().shutdown();
		
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyy HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		String date = dtf.format(now);
		
		if(!(members.size() >= 1)) {
			log.setText("Activity Log:\n\n[" + date + "]\n\nCirculate Member\n-----------------------\nTarget Guild: " + c.getGuild().getName() + "\nTarget Channel: " + c.getName()
				+ "\n\nThere is no one on the voice channel. Terminating...");
			closeBot();
			return;
		}
		
		JFrame cf = new JFrame("Circulator");
		JPanel panel = new JPanel();
		
		panel.setLayout(null);
		
		cf.pack();
		cf.setSize(275,340);
		cf.setResizable(false);
		cf.getContentPane().add(panel);
		cf.setVisible(true);
		cf.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		WindowListener exitListener = new WindowAdapter() {
		    @Override
		    public void windowClosing(WindowEvent e) {
		    	cf.dispose();
		        closeBot();
		    }
		};
		
		cf.addWindowListener(exitListener);
		
		DefaultListModel<String> mem = new DefaultListModel<String>();
		
		for(Member m : members)
			mem.addElement(m.getEffectiveName());
		
		memberSelector = new JList(mem);
		
		circulateN = new JButton("Circulate!");
		circulateN.setBounds(20,240,220,40);
		circulateN.setBackground(new Color(43,68,155));
		circulateN.setForeground(Color.WHITE);
		circulateN.setFont(new Font("Arial", Font.BOLD, 11));
		circulateN.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		log.setText("Activity Log:\n\n[" + date + "]\n\nCirculate Member\n-----------------------\nTarget Guild: " + c.getGuild().getName() + "\nTarget Channel: " + c.getName()
				+ "\nTarget Member: " + members.get(memberSelector.getSelectedIndex()).getEffectiveName() + "\n\nCirculating voice channels... ");
	    		
	    		jda = null;
				try {
					circulateTask(v, members.get(memberSelector.getSelectedIndex()), channels);
				} catch (LoginException | InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	    	}
	    });
		
		memberSelector.setBackground(new Color(43,68,155));
		memberSelector.setForeground(Color.WHITE);
		memberSelector.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		memberSelector.setLayoutOrientation(JList.VERTICAL);
		memberSelector.setFont(new Font("Arial", Font.PLAIN, 12));
		memberSelector.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		memberSelector.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
            	if (!e.getValueIsAdjusting()) {
            		circulateN.setEnabled(true);
            	}
            }
        });
		
		JScrollPane memberScr = new JScrollPane(memberSelector);
		memberScr.setBounds(20,20,220,200);
		memberScr.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
			@Override
			protected void configureScrollBarColors() {
				this.thumbColor = Color.WHITE;
				this.trackColor = new Color(54,86,197);
			}
		
			@Override
			protected JButton createDecreaseButton(int orientation) {
				JButton button = super.createDecreaseButton(orientation);
				button.setBackground(Color.WHITE);
				return button;
			}
				
			@Override
			protected JButton createIncreaseButton(int orientation) {
				JButton button = super.createIncreaseButton(orientation);
				button.setBackground(Color.WHITE);
				return button;
			}
		});
		memberScr.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		memberScr.getHorizontalScrollBar().setUI(new BasicScrollBarUI() {
			@Override
			protected void configureScrollBarColors() {
				this.thumbColor = Color.WHITE;
				this.trackColor = new Color(54,86,197);
			}
		
			@Override
			protected JButton createDecreaseButton(int orientation) {
				JButton button = super.createDecreaseButton(orientation);
				button.setBackground(Color.WHITE);
				return button;
			}
				
			@Override
			protected JButton createIncreaseButton(int orientation) {
				JButton button = super.createIncreaseButton(orientation);
				button.setBackground(Color.WHITE);
				return button;
			}
		});
		memberScr.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		circulateTimer(true);
		
		panel.add(memberScr);
		panel.add(circulateN);
		panel.setBackground(new Color(114,137,217));
		
		panel.revalidate();
		panel.repaint();
	}
	
	public static void circulateTask(VoiceChannel channel, Member member, List<VoiceChannel> channels) throws LoginException, InterruptedException {
		jda = JDABuilder.createDefault(DISCORD_TOKEN).setAutoReconnect(true).build().awaitReady();
		
		channel = jda.getVoiceChannelById(channel.getId());
		
    	for(int i = 0; i <= channels.size(); i++) {
    		if(i < channels.size())
    			c = jda.getVoiceChannelById(channels.get(i).getId());
    		else
    			c = channel;

    		if(member.getVoiceState().inVoiceChannel()) {
    			try {
					TimeUnit.MILLISECONDS.sleep(135);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
    			
    			channel.getGuild().moveVoiceMember(member, c).complete();
    		} else {
    			break;
    		}
		}
    	
    	log.setText(log.getText() + "Done!");
    	
    	jda.shutdownNow();
		OkHttpClient client = jda.getHttpClient();
		client.connectionPool().evictAll();
		client.dispatcher().executorService().shutdown();
		
		circulateTimer(false);
	}
	
	public static void circulateTimer(boolean isFirst) {
		circulateN.setText("5");
		memberSelector.setEnabled(false);
		circulateN.setEnabled(false);
		
		Timer timer = new Timer();
		
		TimerTask four = new TimerTask() {
			public void run() {
				circulateN.setText("4");
			}
		};
		
		TimerTask three = new TimerTask() {
			public void run() {
				circulateN.setText("3");
			}
		};
		
		TimerTask two = new TimerTask() {
			public void run() {
				circulateN.setText("2");
			}
		};
		
		TimerTask one = new TimerTask() {
			public void run() {
				circulateN.setText("1");
			}
		};
		
		TimerTask zero = new TimerTask() {
			public void run() {
				circulateN.setText("Circulate!");
				memberSelector.setEnabled(true);
				circulateN.setEnabled(!isFirst);
				
				timer.cancel();
			}
		};
		
		timer.schedule(four, 1000);
		timer.schedule(three, 2000);
		timer.schedule(two, 3000);
		timer.schedule(one, 4000);
		timer.schedule(zero, 5000);
	}
}
