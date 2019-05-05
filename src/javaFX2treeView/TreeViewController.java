package javaFX2treeView;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class TreeViewController {

	@FXML
	private TreeView<String> trv01;
	@FXML
	private Button btn01;
	@FXML
	private TextArea txa01;

	@FXML
	private TreeView<String> trv02;
	@FXML
	private Button btn02;
	@FXML
	private TextArea txa02;

	private ArrayList<String> cocktailNames = new ArrayList<>();
	private HashMap<String, String> cocktailHmap = new HashMap<>();

	@FXML
	void initialize() {

		// get Cocktail list
		{
			URL url = this.getClass().getResource("res/cocktail.csv");
			OpCsv csv = new OpCsv(url);

			TreeMap<Integer, String[]> map = csv.getCsv();
			Iterator<Integer> it = map.keySet().iterator();
			while (it.hasNext()) {
				int no = it.next();
				String[] words = map.get(no);
				String ename = words[0];
				String jname = words[1];
				if (cocktailHmap.containsKey(ename)) {
					String duplicateKey = ename + " ## duplicate ##";
					cocktailHmap.put(duplicateKey, jname + " (T_T)");
				}
				else {
					cocktailHmap.put(ename, jname);
				}
			}

			Iterator<String> itCocktail = (new TreeSet<>(cocktailHmap.keySet())).iterator(); // sort the key
			while (itCocktail.hasNext()) {
				cocktailNames.add(itCocktail.next());
			}
		}

		// Using ICON
		assert trv01 != null : "fx:id=\"trv01\" was not injected: check your FXML file 'TreeView.fxml'.";
		assert btn01 != null : "fx:id=\"btn01\" was not injected: check your FXML file 'TreeView.fxml'.";
		assert txa01 != null : "fx:id=\"txa01\" was not injected: check your FXML file 'TreeView.fxml'.";
		this.trv01.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		Image rootImage = new Image(this.getClass().getResourceAsStream("res/shaker.png"));
		ImageView rootImageView = new ImageView(rootImage);
		TreeItem<String> rootItem01 = new TreeItem<>("Cocktail of gin-based", rootImageView);
		rootItem01.setExpanded(true);
		Image cocktailImage = new Image(this.getClass().getResourceAsStream("res/blueGlass.png"));
		for (String name : cocktailNames) {
			ImageView cocktailImageView = new ImageView(cocktailImage);
			TreeItem<String> cocktailItem = new TreeItem<>(name, cocktailImageView);
			rootItem01.getChildren().add(cocktailItem);
		}
		this.trv01.setRoot(rootItem01);
		this.trv01.setUserData(cocktailHmap);

		// Using Check Box Tree
		assert trv02 != null : "fx:id=\"trv02\" was not injected: check your FXML file 'TreeView.fxml'.";
		assert btn02 != null : "fx:id=\"btn02\" was not injected: check your FXML file 'TreeView.fxml'.";
		assert txa02 != null : "fx:id=\"txa02\" was not injected: check your FXML file 'TreeView.fxml'.";
		CheckBoxTreeItem<String> rootItem02 = new CheckBoxTreeItem<>("Cocktail of gin-based");
		rootItem02.setExpanded(true);
		this.trv02.setCellFactory(CheckBoxTreeCell.<String> forTreeView());
		for (String name : cocktailNames) {
			CheckBoxTreeItem<String> cocktailItem = new CheckBoxTreeItem<>(name);

			CheckBoxTreeItem<String> child = new CheckBoxTreeItem<>(name + "<child>");
			cocktailItem.getChildren().add(child);

			rootItem02.getChildren().add(cocktailItem);
		}
		this.trv02.setRoot(rootItem02);
		this.trv02.setUserData(cocktailHmap);
	}

	// Using ICON
	@FXML
	void btn01OnAction(ActionEvent event) {
		this.setTxa01();
	}

	@FXML
	void trv01DoubleClick(MouseEvent e) {
		if (e.getButton().equals(MouseButton.PRIMARY)) {
			if (e.getClickCount() == 2) {
				this.setTxa01();
			}
		}
	}

	private void setTxa01() {

		ObservableList<TreeItem<String>> selected = FXCollections.observableArrayList(this.trv01
				.getSelectionModel().getSelectedItems());
		if (selected != null) {
			@SuppressWarnings("unchecked")
			HashMap<String, String> mp = (HashMap<String, String>) this.trv01.getUserData();

			StringBuffer s = new StringBuffer();
			for (TreeItem<String> item : selected) {
				String name = item.getValue();
				if (item.isLeaf()) {
					String jname = mp.get(name);
					s.append(name + " (" + jname + ")\n");
				}
			}

			this.txa01.setText(s.toString());
			this.trv01.getSelectionModel().clearSelection();
		}
	}

	// Using Check Box Tree
	@FXML
	void btn02OnAction(ActionEvent event) {
		this.setTxa02();
	}

	private void setTxa02() {
		CheckBoxTreeItem<String> rootItem = (CheckBoxTreeItem<String>) this.trv02.getRoot();
		@SuppressWarnings("unchecked")
		HashMap<String, String> mp = (HashMap<String, String>) this.trv02.getUserData();

		String s = getCocktailNames(rootItem, mp);
		this.txa02.setText(s);
	}

	private String getCocktailNames(CheckBoxTreeItem<String> p, HashMap<String, String> m) {
		StringBuffer s = new StringBuffer();

		Iterator<TreeItem<String>> it = p.getChildren().iterator();
		while (it.hasNext()) {
			CheckBoxTreeItem<String> item = (CheckBoxTreeItem<String>) it.next();
			if (item.isSelected()) {
				String jname = m.get(item.getValue());
				if (jname == null) {
					s.append(item.getValue() + " (-)\n");
				}
				else {
					s.append(item.getValue() + " (" + jname + ")" + "\n");
				}
			}

			if (!item.isLeaf()) {
				s.append(getCocktailNames(item, m)); // recursive
			}
		}

		return s.toString();
	}
}
