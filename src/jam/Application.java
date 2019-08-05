package jam;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import javax.swing.text.Document;

import org.apache.commons.io.FileUtils;
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

public class Application {

	private static Text txtDir;
	private static Text txtName;
	private static Text txtText;
	//private static Tree tree;

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
	static void getRootPath(Tree tree) {
		File[] roots = File.listRoots();
		for (int i = 0; i < roots.length; i++) {
			TreeItem root = new TreeItem(tree, 0);
			root.setText(roots[i].toString());
			root.setData(roots[i]);
			new TreeItem(root, 0);
		}
	}

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) throws IOException {

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

		final Tree tree = new Tree(composite, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER | SWT.VIRTUAL);
		tree.setBounds(10, 95, 285, 380);
		tree.setHeaderVisible(true);
		final Menu headerMenu = new Menu(shell, SWT.POP_UP);
		final TreeColumn columnName = new TreeColumn(tree, SWT.NONE);
		columnName.setText("Name");
		columnName.setWidth(150);
		createMenuItem(headerMenu, columnName);
		final TreeColumn columnSize = new TreeColumn(tree, SWT.NONE);
		columnSize.setText("Size");
		columnSize.setWidth(60);
		createMenuItem(headerMenu, columnSize);
		final TreeColumn columnType = new TreeColumn(tree, SWT.NONE);
		columnType.setText("Type");
		columnType.setWidth(75);
		createMenuItem(headerMenu, columnType);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = 2;
		
		//-----------------First inicialization of tree-------------------------------------------------
		getRootPath(tree);
		
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
				txtText.setText("");
				txtText.forceFocus();
				tree.clearAll(true);
			}
		});

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

		Button btnNext = new Button(composite, SWT.NONE);
		btnNext.setBounds(500, 55, 75, 25);
		btnNext.setText("Next");
		btnNext.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				keyword = txtText.getText();
				styledText.redraw();
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
					if (file.getName().contains(".txt") || file.getName().contains(".log")) {
						content = Files.lines(Paths.get(filePath)).reduce("", (a, b) -> a + "" + b + "\n");
						styledText.setText(content);
					} else {
						return;
					}
				} catch (IOException e) { // TODO Auto-generated catch block e.printStackTrace(); }

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
}
