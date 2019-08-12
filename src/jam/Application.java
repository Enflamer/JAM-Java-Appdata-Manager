package jam;

import java.io.File;
import java.io.IOException;
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
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.layout.GridLayout;

public class Application {
	public Text txtDir, 
				txtName, 
				txtText, 
				txtTimer;
	public int selected = 0;
	public Display display = new Display();
	public Shell shell = new Shell(display, SWT.SHELL_TRIM & (~SWT.RESIZE));
	public StyledText styledText;
	public String keyword;
	public ArrayList<Integer> listOfCords = new ArrayList<Integer>();
	public Tree tree;
	public Composite composite;
	public CTabFolder tabFolder;
	public Button btnSearch;

	/**
	 * Builds the new column viewer instance.
	 *
	 * @param parent
	 *            the parent composite for the viewer.
	 * @param column
	 *           	 tree column.
	 */
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
	
	/**
	 * Highlight text in yellow background color.
	 *
	 * @param startOffset
	 *            start point of word to highlight.
	 * @param length
	 *            end point of word to highlight.
	 * @return highlighted text.
	 */
	private StyleRange getHighlightStyle(int startOffset, int length) {
		StyleRange styleRange = new StyleRange();
		styleRange.start = startOffset;
		styleRange.length = length;
		styleRange.background = shell.getDisplay().getSystemColor(SWT.COLOR_YELLOW);
		return styleRange;
	}
	
	/**
	 * Takes a root path in file system.
	 *
	 * @param tree
	 *            file system tree.
	 */
	public void getRootPath(Tree tree) {
		File[] roots = File.listRoots();
		for (int i = 0; i < roots.length; i++) {
			TreeItem root = new TreeItem(tree, 0);
			root.setText(roots[i].toString());
			root.setData(roots[i]);
			new TreeItem(root, 0);
		}
	}
	
	/**
	 * System word highlighting by next and prev.
	 *
	 * @param tv
	 *            swt text field.
	 * @param keyword
	 *            word to highlight.
	 * @param selected
	 *            pointer in list of witch word to highlight.
	 * @return pointer.
	 */
	private int highLighting(StyledText tv, String keyword, int selected) {
		if (selected < 0) {
			selected = 0;
		} else if (listOfCords.isEmpty()) {
			return 0;
		} else {
			styledText.setSelection(listOfCords.get(selected), listOfCords.get(selected) + keyword.length());
		}
		return selected;
	}

	/**
	 * Convert all text in String.
	 *
	 * @param file
	 *            file to read.
	 * @return text String.
	 */
	public String getFileContent(File file) throws IOException {
		if (file.isDirectory()) {
			return "";
		}
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
			return fileContent;
		}
	}

	Application() {
		shell.setSize(800, 571);
		shell.setText("JAM - Java Appdata Manager");
		shell.setLayout(new GridLayout(1, false));

		CTabFolder tabFolder = new CTabFolder(shell, SWT.BORDER | SWT.FLAT);
		GridData gd_tabFolder = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_tabFolder.widthHint = 789;
		gd_tabFolder.heightHint = 538;
		tabFolder.setLayoutData(gd_tabFolder);
		tabFolder.setSelectionBackground(
				Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));

		CTabItem tbtmFirstTab = new CTabItem(tabFolder, SWT.NONE);
		tbtmFirstTab.setFont(SWTResourceManager.getFont("Corbel", 12, SWT.NORMAL));
		tbtmFirstTab.setText("Main tab");

		Composite composite = new Composite(tabFolder, SWT.NONE);
		tbtmFirstTab.setControl(composite);
		composite.setLayout(null);

		run(composite);
		

	}

	public void run(Composite composite) {
		// -----------------First initialization of
		// tree-----------------------------------------
		final Tree tree = new Tree(composite, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER | SWT.VIRTUAL);
		tree.setBounds(10, 95, 285, 380);
		tree.setHeaderVisible(true);
		final Menu headerMenu = new Menu(shell, SWT.POP_UP);
		final TreeColumn columnName = new TreeColumn(tree, SWT.CENTER);
		columnName.setText("Name");
		columnName.setWidth(285);
		createMenuItem(headerMenu, columnName);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = 2;

		// -----------------First tree
		// filling-------------------------------------------
		getRootPath(tree);

		// -----------------Buttons and labels
		// init------------------------------------------
		Label lblHighlighting = new Label(composite, SWT.CENTER);
		lblHighlighting.setFont(SWTResourceManager.getFont("Corbel", 12, SWT.NORMAL));
		lblHighlighting.setBounds(503, 37, 232, 19);
		lblHighlighting.setText("Highlighting");
		
		Label labline = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
		labline.setBounds(496, 36, 263, 2);
		Label labline2 = new Label(composite, SWT.SEPARATOR | SWT.VERTICAL);
		labline2.setBounds(496, 36, 2, 58);
		Label labline3 = new Label(composite, SWT.SEPARATOR);
		labline3.setBounds(757, 36, 2, 60);
		
		Label lblDirectory = new Label(composite, SWT.NONE);
		lblDirectory.setFont(SWTResourceManager.getFont("Corbel", 14, SWT.NORMAL));
		lblDirectory.setBounds(10, 5, 80, 20);
		lblDirectory.setText("Directory");

		txtDir = new Text(composite, SWT.BORDER | SWT.WRAP);
		txtDir.setFont(SWTResourceManager.getFont("Gill Sans MT", 12, SWT.NORMAL));
		txtDir.setBounds(100, 4, 390, 25);
		txtDir.setText("C:\\");

		Label lblFileName = new Label(composite, SWT.NONE);
		lblFileName.setFont(SWTResourceManager.getFont("Corbel", 14, SWT.NORMAL));
		lblFileName.setBounds(10, 30, 75, 21);
		lblFileName.setText("File name");

		txtName = new Text(composite, SWT.BORDER);
		txtName.setFont(SWTResourceManager.getFont("Gill Sans MT", 12, SWT.NORMAL));
		txtName.setBounds(100, 30, 390, 25);
		txtName.setText(".log");

		Label lblSearchText = new Label(composite, SWT.NONE);
		lblSearchText.setFont(SWTResourceManager.getFont("Corbel", 14, SWT.NORMAL));
		lblSearchText.setBounds(5, 57, 93, 29);
		lblSearchText.setText("Search text");

		txtText = new Text(composite, SWT.BORDER);
		txtText.setFont(SWTResourceManager.getFont("Gill Sans MT", 12, SWT.NORMAL));
		txtText.setBounds(100, 57, 390, 25);

		Button btnSearch = new Button(composite, SWT.NONE | SWT.WRAP);
		btnSearch.setFont(SWTResourceManager.getFont("Corbel", 12, SWT.NORMAL));
		btnSearch.setText("Search");
		btnSearch.setBounds(549, 5, 75, 25);
		btnSearch.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				String searchText = txtText.getText();
				String dirPath = txtDir.getText();
				if (txtText.getText() == "") {
					return;
				}
				tree.removeAll();
				long time1 = System.currentTimeMillis();
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
				long time2 = System.currentTimeMillis();
				txtTimer.setText((time2 - time1) + " ms");
			}
		});

		Button btnClear = new Button(composite, SWT.NONE);
		btnClear.setFont(SWTResourceManager.getFont("Corbel", 12, SWT.NORMAL));
		btnClear.setBounds(629, 5, 75, 25);
		btnClear.setText("Clear");
		btnClear.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				TreeItem item = (TreeItem) e.item;
				TreeItem[] items = tree.getItems();
				for (int i = 0; i < items.length; i++) {
					tree.removeAll();
				}
				getRootPath(tree);
				styledText.setText("");
				txtDir.setText("C:\\");
				txtName.setText(".log");
				txtText.setText("");
			}
		});

		Button btnNext = new Button(composite, SWT.NONE);
		btnNext.setFont(SWTResourceManager.getFont("Corbel", 12, SWT.NORMAL));
		btnNext.setText("Next");
		btnNext.setBounds(660, 60, 75, 25);
		btnNext.setSelection(true);
		btnNext.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				if (selected >= listOfCords.size() - 1) {
					selected = -1;
				} else {
					selected++;
					selected = highLighting(styledText, txtText.getText(), selected);
				}
			}
		});

		Button btnAll = new Button(composite, SWT.NONE);
		btnAll.setFont(SWTResourceManager.getFont("Corbel", 12, SWT.NORMAL));
		btnAll.setBounds(580, 60, 75, 25);
		btnAll.setText("All");
		btnAll.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				keyword = txtText.getText();
				styledText.redraw();
			}
		});

		Button btnPrevious = new Button(composite, SWT.NONE);
		btnPrevious.setFont(SWTResourceManager.getFont("Corbel", 12, SWT.NORMAL));
		btnPrevious.setText("Previous");
		btnPrevious.setBounds(499, 60, 75, 25);
		btnPrevious.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				if (selected == 0) {
					selected = listOfCords.size();
				} else {
					selected--;
					selected = highLighting(styledText, txtText.getText(), selected);
				}
			}
		});

		txtTimer = new Text(composite, SWT.READ_ONLY);
		txtTimer.setFont(SWTResourceManager.getFont("Gill Sans MT", 12, SWT.NORMAL));
		txtTimer.setBounds(579, 479, 156, 20);

		Label lblSearchTime = new Label(composite, SWT.NONE);
		lblSearchTime.setFont(SWTResourceManager.getFont("Corbel", 14, SWT.NORMAL));
		lblSearchTime.setBounds(487, 479, 101, 20);
		lblSearchTime.setText("Search time:");

		// -----------------Output area init------------------------------------------
		styledText = new StyledText(composite,
				SWT.MULTI | SWT.READ_ONLY | SWT.WRAP | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		styledText.setLocation(301, 95);
		styledText.setSize(458, 380);
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
						selected = 0;
						TreeItem item = (TreeItem) e.item;
						File file = (File) item.getData();
						String searchText = txtText.getText();
						long time1 = System.currentTimeMillis();

						if (file.isDirectory()) {
							return;
						}
						try {
							styledText.setText(getFileContent(file));
						} catch (IOException e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}
						if (searchText == "") {
							return;
						}
						if (listOfCords.size() > 0) {
							selected = 0;
							listOfCords.clear();
						} else {
							String fileContent;
							try {
								fileContent = getFileContent(file);
								if (fileContent.contains(searchText)) {

									Matcher m = Pattern.compile(searchText + "\\b").matcher(fileContent);

									while (m.find()) {
										listOfCords.add(m.start());
									}
									styledText.setSelection(listOfCords.get(selected),
											listOfCords.get(selected) + searchText.length());

								}
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}
						long time2 = System.currentTimeMillis();
						txtTimer.setText((time2 - time1) + " ms");
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
	public static void main(String[] args) throws IOException {new Application();}
}
