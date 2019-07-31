package jam;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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
	private RandomAccessFile file;
	private String filePath = txtDir.getText();
	private static app worker;

	
	
	public app(String filePath) {
        this.filePath = filePath;
    }
	
    public long goTo(int num) throws IOException {
        // инициализируем класс RandomAccessFile 
        // в параметры передаем путь к файлу 
        // и модификатор который говорит, что файл откроется только для чтения
        file = new RandomAccessFile(filePath, "r");

        // переходим на num символ
        file.seek(num);

        // получаем текущее состояние курсора в файле
        long pointer = file.getFilePointer();
        file.close();

        return pointer;
    }

	 public String read() throws IOException {
	        file = new RandomAccessFile(filePath, "r");
	        String res = "";
	        int b = file.read();
	        // побитово читаем символы и плюсуем их в строку
	        while(b != -1){
	            res = res + (char)b;
	            b = file.read();
	        }
	        file.close();

	        return res;
	    }
	 
	 public String readFrom(int numberSymbol) throws IOException {
	        // открываем файл для чтения
	        file = new RandomAccessFile(filePath, "r");
	        String res = "";

	        // ставим указатель на нужный вам символ
	        file.seek(numberSymbol);
	        int b = file.read();

	        // побитово читаем и добавляем символы в строку
	        while(b != -1){
	            res = res + (char)b;

	            b = file.read();
	        }
	        file.close();

	        return res;
	    }

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

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) throws IOException {
		
		//String searchWord = txtText.getText();
		//FileInputStream fis = new FileInputStream(new File(txtDir.getText()));

		// Инициализация окна приложения
		Display display = Display.getDefault();
		Shell shell = new Shell();
		shell.setSize(764, 560);
		shell.setText("JAM - Java Appdata Manager");
		shell.setLayout(null);
		// RGB color = shell.getBackground().getRGB();

		// Инициализация вкладок
		CTabFolder tabFolder_1 = new CTabFolder(shell, SWT.BORDER);
		tabFolder_1.setBounds(0, 0, 738, 511);
		tabFolder_1.setSelectionBackground(
				Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));

		// Инициализация первой вкладки
		CTabItem tbtmFirstTab = new CTabItem(tabFolder_1, SWT.NONE);
		tbtmFirstTab.setText("First tab");
		// Инициализация страницы вкладки
		Composite composite = new Composite(tabFolder_1, SWT.NONE);
		tbtmFirstTab.setControl(composite);
		composite.setLayout(null);

		Label lblDirectory = new Label(composite, SWT.NONE);
		lblDirectory.setBounds(10, 10, 79, 15);
		lblDirectory.setText("Directory");

		txtDir = new Text(composite, SWT.BORDER);
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

		Button btnSearch = new Button(composite, SWT.NONE);
		btnSearch.setBounds(500, 55, 75, 25);
		btnSearch.setText("Search");
		btnSearch.addListener(SWT.Selection, new Listener()
		{
		    @Override
		    public void handleEvent(Event event)
		    {
		    	String searchWord = txtText.getText();
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
			                    System.out.println("Найдено в " + i + "-й строке, " + j + "-е слово");
			                }
			                j++;
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
		btnClear.setBounds(578, 55, 75, 25);
		btnClear.setText("Clear");

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

		txtOutput = new Text(composite,
				SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		txtOutput.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		txtOutput.setBounds(301, 95, 421, 381);

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
					txtOutput.setText(content);
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
