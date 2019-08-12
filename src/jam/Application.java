package jam;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.stream.Stream;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;

public class Application {

	private Text txtDir;
	private Text txtName;
	private Text txtText;
	private int selected = 0;
	private Display display = new Display();
	private Shell shell = new Shell(display);
	private StyledText styledText;
	private String keyword;
	private ArrayList<Integer> listOfCords = new ArrayList<Integer>();
	private Text txtTimer;

	public void createMenuItem(Menu parent, final TreeColumn column) {
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

	private StyleRange getHighlightStyle(int startOffset, int length) {
		StyleRange styleRange = new StyleRange();
		styleRange.start = startOffset;
		styleRange.length = length;
		styleRange.background = shell.getDisplay().getSystemColor(SWT.COLOR_YELLOW);
		return styleRange;
	}

	public void getRootPath(Tree tree) {
		File[] roots = File.listRoots();
		for (int i = 0; i < roots.length; i++) {
			TreeItem root = new TreeItem(tree, 0);
			root.setText(roots[i].toString());
			root.setData(roots[i]);
			new TreeItem(root, 0);
		}
	}

	private int highLighting(StyledText tv, String keyword, int selected) {
		if (selected < 0) {
			selected = 0;
		}
		styledText.setSelection(listOfCords.get(selected), listOfCords.get(selected) + keyword.length());
		return selected;
	}

	public String getFileContent(FileInputStream fis, String encoding) throws IOException {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(fis, encoding))) {
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line);
				sb.append('\n');
			}
			return sb.toString();
		}
	}

	Application() {

		shell.setSize(800, 580);
		shell.setText("JAM - Java Appdata Manager");
		shell.setLayout(null);

		CTabFolder tabFolder_1 = new CTabFolder(shell, SWT.BORDER);
		tabFolder_1.setBounds(5, 5, 775, 526);
		tabFolder_1.setSelectionBackground(
				Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));

		CTabItem tbtmFirstTab = new CTabItem(tabFolder_1, SWT.NONE);
		tbtmFirstTab.setText("First tab");

		Composite composite = new Composite(tabFolder_1, SWT.NONE);
		tbtmFirstTab.setControl(composite);
		composite.setLayout(null);

		// -----------------First initialization of
		// tree-----------------------------------------
		final Tree tree = new Tree(composite, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER | SWT.VIRTUAL);
		tree.setBounds(10, 95, 285, 380);
		tree.setHeaderVisible(true);
		final Menu headerMenu = new Menu(shell, SWT.POP_UP);
		final TreeColumn columnName = new TreeColumn(tree, SWT.NONE);
		columnName.setText("Name");
		columnName.setWidth(283);
		createMenuItem(headerMenu, columnName);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = 2;

		// -----------------First tree
		// filling-------------------------------------------
		getRootPath(tree);

		// -----------------Buttons and labels
		// init------------------------------------------
		Label lblDirectory = new Label(composite, SWT.NONE);
		lblDirectory.setBounds(10, 10, 80, 15);
		lblDirectory.setText("Directory");

		txtDir = new Text(composite, SWT.BORDER | SWT.WRAP);
		txtDir.setBounds(90, 10, 400, 20);
		txtDir.setText("C:\\");

		Label lblFileName = new Label(composite, SWT.NONE);
		lblFileName.setBounds(10, 34, 55, 15);
		lblFileName.setText("File name");

		txtName = new Text(composite, SWT.BORDER);
		txtName.setBounds(90, 35, 400, 20);
		txtName.setText(".log");

		Label lblSearchText = new Label(composite, SWT.NONE);
		lblSearchText.setBounds(10, 60, 80, 15);
		lblSearchText.setText("Search text");

		txtText = new Text(composite, SWT.BORDER);
		txtText.setBounds(90, 60, 400, 20);

		Button btnSearch = new Button(composite, SWT.NONE | SWT.WRAP);
		btnSearch.setBounds(530, 10, 75, 25);
		btnSearch.setText("Search");
		btnSearch.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				String searchText = txtText.getText();
				String dirPath = txtDir.getText();
				tree.removeAll();

				try (Stream<Path> paths = Files.walk(Paths.get(dirPath))) {
					paths.filter(p -> p.toString().endsWith(txtName.getText())).forEach(path -> {
						try {
							String content = new String(Files.readAllBytes(path));
							if (searchText != null && content.contains(searchText)) {
								try {
									TreeItem dir = new TreeItem(tree, 0);
									dir.setText(path.toFile().toString());
									dir.setData(path.toFile());
									new TreeItem(dir, 0);
								} catch (Exception e) {
									// if any error occurs
									e.printStackTrace();
								}

							} else {
								return;
							}
						} catch (IOException e) {
							throw new UncheckedIOException(e);
						}
					});
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

		Button btnClear = new Button(composite, SWT.NONE);
		btnClear.setBounds(630, 10, 75, 25);
		btnClear.setText("Clear");
		btnClear.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});

		Button btnNext = new Button(composite, SWT.NONE);
		btnNext.setBounds(500, 55, 75, 25);
		btnNext.setText("Next");
		btnNext.setSelection(true);
		btnNext.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				selected++;
				selected = highLighting(styledText, txtText.getText(), selected);
			}
		});

		Button btnAll = new Button(composite, SWT.NONE);
		btnAll.setBounds(580, 55, 75, 25);
		btnAll.setText("All");
		btnAll.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				keyword = txtText.getText();
				styledText.redraw();
			}
		});

		Button btnPrevious = new Button(composite, SWT.NONE);
		btnPrevious.setBounds(660, 55, 75, 25);
		btnPrevious.setText("Previous");
		btnPrevious.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				selected--;
				selected = highLighting(styledText, txtText.getText(), selected);
			}
		});

		Label lblWarningFileWill = new Label(composite, SWT.NONE);
		lblWarningFileWill.setBounds(10, 481, 303, 15);
		lblWarningFileWill.setText("Warning! File will open only if you double clicked on it.");

		txtTimer = new Text(composite, SWT.READ_ONLY);
		txtTimer.setBounds(579, 481, 156, 18);
		
		Label lblSearchTime = new Label(composite, SWT.NONE);
		lblSearchTime.setBounds(513, 481, 75, 15);
		lblSearchTime.setText("Search time:");
		
		// -----------------Output area init------------------------------------------
		styledText = new StyledText(composite,
				SWT.MULTI | SWT.READ_ONLY | SWT.WRAP | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		styledText.setLocation(300, 95);
		styledText.setSize(450, 380);
		styledText.setLayoutData(gridData);
		
		styledText.addLineStyleListener(new LineStyleListener() {
			public void lineGetStyle(LineStyleEvent event) {
				if (keyword == null || keyword.length() == 0) {
					event.styles = new StyleRange[0];
					return;
				}

				String line = event.lineText;
				int cursor = -1;

				LinkedList<StyleRange> list = new LinkedList<StyleRange>();
				while ((cursor = line.indexOf(keyword, cursor + 1)) >= 0) {
					list.add(getHighlightStyle(event.lineOffset + cursor, keyword.length()));
				}
				event.styles = (StyleRange[]) list.toArray(new StyleRange[list.size()]);
			}
		});

		// -----------------Tree listeners------------------------------------------
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
		tree.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						//listOfCords.clear();
						TreeItem item = (TreeItem) e.item;
						File file = (File) item.getData();
						String searchText = txtText.getText();

						if (file.isDirectory()) {
							return;
						}
						long time1 = System.currentTimeMillis();
						try (RandomAccessFile reader = new RandomAccessFile(file, "r");
								FileChannel channel = reader.getChannel();
								ByteArrayOutputStream out = new ByteArrayOutputStream()) {

							int bufferSize = 1024;
							if (bufferSize > channel.size()) {
								bufferSize = (int) channel.size();
							}

							ByteBuffer buff = ByteBuffer.allocate(bufferSize);

							while (channel.read(buff) > 0) {
								buff.flip();
								out.write(buff.array());
								buff.clear();
							}

							String fileContent = new String(out.toByteArray(), StandardCharsets.UTF_8);
							styledText.setText(fileContent);

							if (searchText == "") {
								return;
							}
							if (listOfCords.size() > 0) {
								selected = 0;
								listOfCords.clear();
							}
							if (fileContent.contains(searchText)) {

								styledText.getText();
								Matcher m = Pattern.compile(searchText + "\\b").matcher(fileContent);

								while (m.find()) {
									listOfCords.add(m.start());
								}
								styledText.setSelection(listOfCords.get(selected),
										listOfCords.get(selected) + searchText.length());

							}
							// System.out.println(listOfCords);

						} catch (FileNotFoundException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (IOException e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}
						// styledText.setText(fileContent);
						long time2 = System.currentTimeMillis();
						txtTimer.setText((time2 - time1) + " ms");
						// System.out.println("Time taken: " + (time2 - time1) + " ms");
					}
				});
			}
		});

		tree.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				TreeItem item = (TreeItem) e.item;
				if (item == null)
					return;
				final File root = (File) item.getData();
				txtDir.getText();
				txtDir.setText(root.getPath());
				txtDir.setText(root.getPath());
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

	/**
	 * Launch the application.
	 * 
	 * @param args
	 * @return
	 */
	public static void main(String[] args) throws IOException {
		new Application();
	}
}
