package jam;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.Document;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.LineStyleEvent;
import org.eclipse.swt.custom.LineStyleListener;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.wb.swt.SWTResourceManager;

public class app {

	private static Text txtDir;
	private static Text txtName;
	private static Text txtText;
	private static Text txtOutput;
	//private RandomAccessFile file;
    //private String filePath = txtDir.getText();
	
	static Display display = new Display();
	static Shell shell = new Shell(display);

	static StyledText styledText;
	static String keyword;
	
	static void createMenuItem(Menu parent, final TreeColumn column) {
		final MenuItem itemName = new MenuItem(parent, SWT.CHECK);
		itemName.addListener(SWT.Selection, event -> {
			if (itemName.getSelection()) {
				column.setWidth(150);
				column.setResizable(true);
			} else {
				column.setWidth(0);
				column.setResizable(false);
			}
		});
	}
	
	  private static StyleRange getHighlightStyle(int startOffset, int length) {
		    StyleRange styleRange = new StyleRange();
		    styleRange.start = startOffset;
		    styleRange.length = length;
		    styleRange.background = shell.getDisplay().getSystemColor(SWT.COLOR_YELLOW);
		    return styleRange;
		  }
	/*
	private static void setSelection(Text text, String query)
	{
		String comboText = text.getText();
	    int index = comboText.indexOf(query);

	    if(index != -1)
	        text.setSelection(new Point(index, index + query.length()));
	}*/
	
	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) throws IOException {
		

		
		LinkedList<String> searchList = new LinkedList<String>();
		LinkedList<Integer> wordList = new LinkedList<Integer>();
		LinkedList<Integer> spaceList = new LinkedList<Integer>();

		// Инициализация окна приложения
		shell.setSize(800, 560);
		shell.setText("JAM - Java Appdata Manager");
		shell.setLayout(null);
		// RGB color = shell.getBackground().getRGB();

		// Инициализация вкладок
		CTabFolder tabFolder_1 = new CTabFolder(shell, SWT.BORDER);
		tabFolder_1.setBounds(0, 0, 774, 511);
		tabFolder_1.setSelectionBackground(
				Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));

		// Инициализация первой вкладки
		CTabItem tbtmFirstTab = new CTabItem(tabFolder_1, SWT.NONE);
		tbtmFirstTab.setText("First tab");
		// Инициализация страницы вкладки
		Composite composite = new Composite(tabFolder_1, SWT.NONE);
		tbtmFirstTab.setControl(composite);
		composite.setLayout(null);

		Label lblDirectory = new Label(composite, SWT.NONE );
		lblDirectory.setBounds(10, 10, 79, 15);
		lblDirectory.setText("Directory");

		txtDir = new Text(composite, SWT.BORDER | SWT.WRAP);
		txtDir.setBounds(91, 7, 401, 21);
		txtDir.setText("C:\\");

		Label lblFileName = new Label(composite, SWT.NONE);
		lblFileName.setBounds(10, 34, 55, 15);
		lblFileName.setText("File name");

		txtName = new Text(composite, SWT.BORDER);
		txtName.setBounds(91, 31, 401, 21);
		txtName.setText("*.log");

		Label lblSearchText = new Label(composite, SWT.NONE);
		lblSearchText.setBounds(10, 60, 79, 15);
		lblSearchText.setText("Search text");

		txtText = new Text(composite, SWT.BORDER);
		txtText.setBounds(91, 58, 401, 21);

		Button btnSearch = new Button(composite, SWT.NONE | SWT.WRAP);
		btnSearch.setBounds(536, 5, 75, 25);
		btnSearch.setText("Search");
		btnSearch.addListener(SWT.Selection, new Listener()
		{
		    @Override
		    public void handleEvent(Event event)
		    {	
		    	String searchText = txtOutput.getText();
		    	int sizeOfWord = 0;
		    	List<String> tokens = new ArrayList<String>();
		    	tokens.add(txtText.getText());
		    	String patternString = "\\b(" + StringUtils.join(tokens, "|") + ")\\b";

		    	
		    	Pattern pattern = Pattern.compile(patternString);
		    	Matcher matcher = pattern.matcher(searchText);

		    	while (matcher.find()) {
		    	    System.out.println(matcher.group(1));
		    	    wordList.add(matcher.end());
		    	}
		    	
		    	
		    	
		    	for (int i = 0; i < wordList.size(); i++) {
		    		System.out.println(wordList.get(i));
		    	}
		    	
		    	
		    	
		    	
		    	
		    	
		    	
		    	
		    	
		    	
		    	
		    	
		    	
		    	
		    	
		    	
		    	
		    	/*
				FileInputStream fis;
				try {
					fis = new FileInputStream(new File(txtDir.getText()));
					byte[] content = new byte[fis.available()];
					fis.read(content);
			        fis.close();
			        String[] lines = new String(content, "Cp1251").split("\n"); // кодировку указать нужную
			        int i = 1;
			        for (String line : lines) {
			            String[] words = line.split(" ");
			            int j = 1;
			            for (String word : words) {
			                if (word.equalsIgnoreCase(searchWord)) {
			                	wordList.add(j);
			                	//System.out.println(word.lastIndexOf(word));

			                	
			                	//txtOutput.setSelection(j);
			                    System.out.println("Найдено " + j + "-е слово");
			                }
			                j++;
			                //searchList3.add(txtOutput.getText().lastIndexOf(j-1));
			            }
			            i++;
			        }
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//for (int k = 1; k < wordList.size();k++) {
				//	spaceList.add(txtOutput.getText().lastIndexOf(k-1));
		    	//}
				spaceList.add(0);
				String allText = txtOutput.getText();
				System.out.println(allText);
				String[] words = allText.split("\\s+");
				String s = "p";
				for(int i=0; i< allText.length(); i++){
			        if(allText.charAt(i) == ' ') spaceList.add(i);
			    }
				spaceList.add(spaceList.get(spaceList.size()-1));
				//String[] arr = allText.split("");
				//for (int k = 1; k < allText.length();k++) {
					//boolean isWhitespace = allText.matches("^\\s*$");
					//int index = allText.indexOf(" ");
					//spaceList.add(index);

		  
		    	//}
				//spaceList.add(spaceList.indexOf(spaceList.size()));
				for(int i=0; i< spaceList.size(); i++){
					System.out.println(spaceList.get(i));
			    }*/

		    }
		});
		
				/*
				double count = 0, countBuffer = 0, countLine = 0;
				String lineNumber = "";
				String filePath = txtDir.getText();
				BufferedReader br;
				String inputSearch = txtText.getText();
				String line = "";

				try {
					br = new BufferedReader(new FileReader(filePath));
					try {
						while ((line = br.readLine()) != null) {
							countLine++;
							System.out.println(line);
							String[] words = line.split(" ");

							for (String word : words) {
								if (word.equals(inputSearch)) {
									count++;
									countBuffer++;
								}
							}

							if (countBuffer > 0) {
								countBuffer = 0;
								lineNumber += countLine + ",";
							}

						}
						br.close();
					} catch (IOException e11) {
						// TODO Auto-generated catch block
						e11.printStackTrace();
					}
				} catch (FileNotFoundException e11) {
					// TODO Auto-generated catch block
					e11.printStackTrace();
				}

				System.out.println("Times found at--" + count);
				System.out.println("Word found at--" + lineNumber);
			*/

		Button btnClear = new Button(composite, SWT.NONE);
		btnClear.setBounds(631, 5, 75, 25);
		btnClear.setText("Clear");
		btnClear.addSelectionListener(new SelectionAdapter() {
        	 
            @Override
            public void widgetSelected(SelectionEvent e) {
                txtText.setText("");
                txtText.forceFocus();
            }
        });

		final Tree tree = new Tree(composite, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
		tree.setBounds(10, 95, 285, 381);
		tree.setHeaderVisible(true);
		final Menu headerMenu = new Menu(shell, SWT.POP_UP);
		final TreeColumn columnName = new TreeColumn(tree, SWT.NONE);
		columnName.setText("Name");
		columnName.setWidth(150);
		createMenuItem(headerMenu, columnName);
		final TreeColumn columnSize = new TreeColumn(tree, SWT.NONE);
		columnSize.setText("Size");
		columnSize.setWidth(59);
		createMenuItem(headerMenu, columnSize);
		final TreeColumn columnType = new TreeColumn(tree, SWT.NONE);
		columnType.setText("Type");
		columnType.setWidth(74);
		createMenuItem(headerMenu, columnType);
	    GridData gridData = new GridData(GridData.FILL_BOTH);
	    gridData.horizontalSpan = 2;    
		
		styledText = new StyledText(composite, SWT.MULTI | SWT.READ_ONLY | SWT.WRAP | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		styledText.setLocation(301, 95);
		styledText.setSize(457, 233);
		styledText.setLayoutData(gridData);
		
		styledText.addLineStyleListener(new LineStyleListener() {
		      public void lineGetStyle(LineStyleEvent event) {
		        if(keyword == null || keyword.length() == 0) {
		          event.styles = new StyleRange[0];
		          return;
		        }
		        
		        String line = event.lineText;
		        int cursor = -1;
		        
		        LinkedList<StyleRange> list = new LinkedList<StyleRange>();
		        while( (cursor = line.indexOf(keyword, cursor+1)) >= 0) {
		          list.add(getHighlightStyle(event.lineOffset+cursor, keyword.length()));
		        }
		        
		        event.styles = (StyleRange[]) list.toArray(new StyleRange[list.size()]);
		      }
		    });
		
	    
		txtOutput = new Text(composite,
				SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		txtOutput.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		txtOutput.setBounds(301, 334, 457, 142);
		
		Button btnNext = new Button(composite, SWT.NONE);
		btnNext.setBounds(498, 55, 75, 25);
		btnNext.setText("Next");
		
		Button btnAll = new Button(composite, SWT.NONE);
		btnAll.setBounds(579, 55, 75, 25);
		btnAll.setText("All");
		btnAll.addListener(SWT.Selection, new Listener()
		{

		    @Override
		    public void handleEvent(Event event)
		    {

		    	//String searchText = txtOutput.getText();
		    	//String wordToFind = txtText.getText();
		    	keyword = txtText.getText();
		        styledText.redraw();
		        	//System.out.println(searchList.get(j));
		    	
		    	
		    	
		    	/*
		    	for (int j = 0; j < searchList.size();j++) {
		        	int index = txtOutput.getText().indexOf(searchList.get(j));
		        	txtOutput.setSelection(new Point(index, index + searchList.get(j).length()));
		        	System.out.println(searchList.get(j));
		    	}*/
		    }
		});
        
		
		Button btnPrevious = new Button(composite, SWT.NONE);
		btnPrevious.setBounds(660, 55, 75, 25);
		btnPrevious.setText("Previous");
		

		final Menu treeMenu = new Menu(shell, SWT.POP_UP);
		MenuItem item = new MenuItem(treeMenu, SWT.PUSH);
		item.setText("Open");
		item = new MenuItem(treeMenu, SWT.PUSH);
		item.setText("Open With");
		new MenuItem(treeMenu, SWT.SEPARATOR);
		item = new MenuItem(treeMenu, SWT.PUSH);
		item.setText("Cut");
		item = new MenuItem(treeMenu, SWT.PUSH);
		item.setText("Copy");
		item = new MenuItem(treeMenu, SWT.PUSH);
		item.setText("Paste");
		new MenuItem(treeMenu, SWT.SEPARATOR);
		item = new MenuItem(treeMenu, SWT.PUSH);
		item.setText("Delete");

		File[] roots = File.listRoots();
		for (int i = 0; i < roots.length; i++) {
			TreeItem root = new TreeItem(tree, 0);
			root.setText(roots[i].toString());
			root.setData(roots[i]);
			new TreeItem(root, 0);
		}

		tree.addListener(SWT.Expand, new Listener() {
			public void handleEvent(final Event event) {

				final TreeItem root = (TreeItem) event.item;
				TreeItem[] items = root.getItems();
				for (int i = 0; i < items.length; i++) {
					if (items[i].getData() != null)
						return;
					items[i].dispose();
				}
				File file = (File) root.getData();
				File[] files = file.listFiles();
				if (files == null)
					return;
				for (int i = 0; i < files.length; i++) {
					TreeItem item = new TreeItem(root, 0);
					item.setText(files[i].getName());
					item.setData(files[i]);
					if (files[i].isDirectory()) {
						new TreeItem(item, 0);
					}
				}
			}
		});
		tree.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e1) {
				String filePath = txtDir.getText();
				TreeItem item = (TreeItem) e1.item;
				File file = (File) item.getData();

				String content;
				try {
					if (file.isDirectory())
						return;
					if(file.getName().contains(".txt") || file.getName().contains(".log")) {
					content = Files.lines(Paths.get(filePath)).reduce("", (a, b) -> a + "" + b + "\n");
					styledText.setText(content);
					}else {
						return;
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		tree.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				
				final TreeItem root = (TreeItem) e.item;
				File file = (File) root.getData();
				if (file.isDirectory()) {
					return;
				}else{
				txtName.setText(file.getName());
				txtName.setText(file.getName());
				}
			}
		});
		
		tree.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				TreeItem item = (TreeItem) e.item;
				if (item == null)
					return;
				final File root = (File) item.getData();
				txtDir.getText();
				txtDir.setText(root.getAbsolutePath());
				txtDir.setText(root.getAbsolutePath());
			}
		});

		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
}
