package cn.org.tpeach.nosql.tools;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.function.Consumer;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import cn.org.tpeach.nosql.constant.PublicConstant;
import cn.org.tpeach.nosql.enums.RedisType;
import cn.org.tpeach.nosql.exception.ServiceException;
import cn.org.tpeach.nosql.redis.bean.RedisTreeItem;
import cn.org.tpeach.nosql.view.component.EasyGBC;
import cn.org.tpeach.nosql.view.jtree.RTreeNode;

public class SwingTools {
	// --------------------------------------监听事件相关开始------------------------------------------------

	/**
	 * 组件大小变化监听
	 *
	 * @param component
	 */
	public static void addComponentResizedListener(Component component, Consumer<ComponentEvent> consumer) {
		if (component != null) {
			component.addComponentListener(new ComponentAdapter() {
				@Override
				public void componentResized(ComponentEvent e) {
					consumer.accept(e);
				}
			});
		}
	}

	/**
	 * 鼠标点击事件
	 *
	 * @param component
	 * @param consumer
	 */
	public static void addMouseClickedListener(JComponent component, Consumer<MouseEvent> consumer) {
		if (component != null) {
			component.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if(component.isEnabled()) {
						consumer.accept(e);
					}
				}
			});
		}
	}

    public static void enterPressesWhenFocused(JTextField textField,ActionListener actionListener) {
        textField.registerKeyboardAction(actionListener,
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false),
                JComponent.WHEN_FOCUSED);

        textField.registerKeyboardAction(actionListener,
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true),
                JComponent.WHEN_FOCUSED);
    }


	// --------------------------------------监听事件相关结束------------------------------------------------
	/**
	 * 
	 * @param parentNode
	 * @param id
	 *            唯一标识 SERVER DATABASE KEY
	 * @param db
	 * @param redisType
	 * @param key
	 * @param name
	 * @param imageIcon
	 * @param path
	 * @param tipText
	 * @return
	 */
	public static RTreeNode addTreeNode(RTreeNode parentNode, RedisTreeItem parentItem, String id, String key,
			String name, Integer db, RedisType redisType, String path, String tipText) {
		if (StringUtils.isBlank(id)) {
			throw new ServiceException("添加节点id为空");
		}
		String originName = name;
		if (parentItem != null && !redisType.equals(RedisType.KEY)
				&& parentItem.getType().equals(RedisType.KEY_NAMESPACE)) {
			originName = parentItem.getOriginName() + PublicConstant.NAMESPACE_SPLIT + name;
		}
		RedisTreeItem redisTreeItem = new RedisTreeItem(id, parentItem, db, key, name, originName, redisType, path,
				tipText);
		RTreeNode node = new RTreeNode(redisTreeItem);
		parentNode.add(node);
		return node;
	}

	public static RTreeNode addServerTreeNode(RTreeNode parentNode, String id, String name, String path,
			String tipText) {
		return addTreeNode(parentNode, null, id, null, name, null, RedisType.SERVER, path, tipText);
	}

	public static RTreeNode addServerTreeNode(RTreeNode parentNode, String id, String name, String path) {
		return addServerTreeNode(parentNode, id, name, path, name);
	}

	public static RTreeNode addDatabaseTreeNode(RTreeNode parentNode, RedisTreeItem parentItem, String name, Integer db,
			String path, String tipText) {
		return addTreeNode(parentNode, parentItem, parentItem.getId(), null, name, db, RedisType.DATABASE, path,
				tipText);
	}

	public static RTreeNode addDatabaseTreeNode(RTreeNode parentNode, RedisTreeItem parentItem, String name, Integer db,
			String path) {
		return addDatabaseTreeNode(parentNode, parentItem, name, db, path, name);
	}

	public static RTreeNode addKeyTreeNode(RTreeNode parentNode, RedisTreeItem parentItem, String key, String name,
			String path, String tipText) {
		return addTreeNode(parentNode, parentItem, parentItem.getId(), key, name, parentItem.getDb(), RedisType.KEY,
				path, tipText);
	}

	public static RTreeNode addKeyNamespaceTreeNode(RTreeNode parentNode, RedisTreeItem parentItem, String key,
			String name, String path, String tipText) {
		return addTreeNode(parentNode, parentItem, parentItem.getId(), key, name, parentItem.getDb(),
				RedisType.KEY_NAMESPACE, path, tipText);
	}

	/**
	 * 展开某个节点的所有子节点
	 * 
	 * @param aTree
	 * @param aNode
	 */
	public static void expandTreeNode(JTree aTree, DefaultMutableTreeNode node) {
		if (node.isLeaf()) {
			return;
		}
		aTree.expandPath(new TreePath(((DefaultMutableTreeNode) node).getPath()));
		int n = node.getChildCount();
		for (int i = 0; i < n; i++) {
			expandTreeNode(aTree, (DefaultMutableTreeNode) node.getChildAt(i));
		}
	}

	// --------------------------------------监听事件相关结束------------------------------------------------
	public static void removeTreeNode(JTree tree, DefaultMutableTreeNode node) {
		((DefaultTreeModel) tree.getModel()).removeNodeFromParent(node);
	}
	/**
	 * 設置奇數偶數行換行顯示
	 * @param table
	 * @param oddBackground
	 * @param evenBackground
	 */
	public static void makeFace(JTable table,Color oddBackground,Color evenBackground) {

		try {
			DefaultTableCellRenderer tcr = new DefaultTableCellRenderer() {
				public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
						boolean hasFocus, int row, int column) {
					if (row % 2 == 0)
						setBackground(oddBackground); // 设置奇数行底色
					else if (row % 2 == 1)
						setBackground(evenBackground); // 设置偶数行底色
					return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				}
			};
			for (int i = 0; i < table.getColumnCount(); i++) {
				table.getColumn(table.getColumnName(i)).setCellRenderer(tcr);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public static void showMessageErrorDialog(Component parentComponent, Object message, String title) {
		JOptionPane.showMessageDialog(parentComponent, message, title, JOptionPane.ERROR_MESSAGE);
	}

	public static void showMessageErrorDialog(Component parentComponent, Object message) {
		JOptionPane.showMessageDialog(parentComponent, message, "错误提示", JOptionPane.ERROR_MESSAGE);
	}

	public static void showMessageMessageDialog(Component parentComponent, Object message, String title) {
		JOptionPane.showMessageDialog(parentComponent, message, title, JOptionPane.WARNING_MESSAGE);
	}

	public static void showMessageInfoDialog(Component parentComponent, Object message, String title) {
		JOptionPane.showMessageDialog(parentComponent, message, title, JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * optionType 为 YES_NO_OPTION
	 * 
	 * @param parentComponent
	 * @param message
	 * @param title
	 * @return
	 */
	public static int showConfirmDialogYNC(Component parentComponent, Object message, String title) {
		return JOptionPane.showConfirmDialog(parentComponent, message, title, JOptionPane.YES_NO_OPTION);
	}

	public static String showInputDialog(Component parentComponent, Object message, String title, Object defaultValue) {
		return (String) JOptionPane.showInputDialog(parentComponent, message, title, JOptionPane.INFORMATION_MESSAGE,
				null, null, defaultValue);
	}
	
	/**
	 * 创建一个面板，面板中心显示一个标签，用于表示某个选项卡需要显示的内容
	 */
	public static JPanel createTextRow(JLabel lable, JComponent field,int width, int rowHeight) {

		return createTextRow(lable, field, width,rowHeight, null);
	}

	/**
	 * 创建一个面板，面板中心显示一个标签，用于表示某个选项卡需要显示的内容
	 */
	public static JPanel createTextRow(JLabel lable, JComponent field,int width, int rowHeight, Color bgcolor) {

		return createTextRow(lable, field, 0.3, 0.7,width, rowHeight, bgcolor,new Insets(10, 10, 0, 0),new Insets(10, 10, 0, 30));
	}
	/**
	 * 创建一个面板，面板中心显示一个标签，用于表示某个选项卡需要显示的内容
	 */
	public static JPanel createTextRow(JLabel lable, JComponent field,int width, int rowHeight, Color bgcolor,Insets labelInsets,Insets fieldInsets) {

		return createTextRow(lable, field, 0.3, 0.7,width, rowHeight, bgcolor,labelInsets,fieldInsets);
	}

	/**
	 * 创建一个面板，面板中心显示一个标签，用于表示某个选项卡需要显示的内容
	 */
	public static JPanel createTextRow(JLabel lable, JComponent field, double lableWeightX, double fieldWeightX,
			int width,int rowHeight, Color bgcolor,Insets labelInsets,Insets fieldInsets) {
		JPanel pannel = getPannelPreferredSize(width, rowHeight);
		pannel.setLayout(new GridBagLayout());
		addLabel(pannel, lable, lableWeightX, 0,labelInsets);
		addTextField(pannel, field, fieldWeightX, 0,fieldInsets);
		if (bgcolor != null) {
			pannel.setBackground(bgcolor);
		}

		return pannel;
	}
	
	public static void addTextField(JComponent component, JComponent field, double fieldWeightX, int row,Insets fieldInsets) {
		component.add(field, EasyGBC.build(1, row, 4, 1).setFill(EasyGBC.HORIZONTAL).setWeight(fieldWeightX, 1.0)
				.resetInsets(fieldInsets).setAnchor(EasyGBC.WEST));
	}

	public static void addLabel(JComponent component, JLabel label, double lableWeightX, int row,Insets labelInsets) {
		component.add(label, EasyGBC.build(0, row, 1, 1).setFill(EasyGBC.HORIZONTAL).setWeight(lableWeightX, 1.0)
				.resetInsets(labelInsets).setAnchor(EasyGBC.EAST));
	}
	public static JPanel getPannelPreferredSize(int width, int height) {
		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(width, height));
		return panel;
	}
	
	public static Dimension getScreenSize() {
		return Toolkit.getDefaultToolkit().getScreenSize();
	}
	

    /**
	 * 统一设置字体，父界面设置之后，所有由父界面进入的子界面都不需要再次设置字体
	 */
	public static void initGlobalFont(Font font) {
		FontUIResource fontRes = new FontUIResource(font);
		for (Enumeration<Object> keys = UIManager.getDefaults().keys(); keys.hasMoreElements();) {
			Object key = keys.nextElement();
			Object value = UIManager.get(key);
			if (value instanceof FontUIResource) {
				UIManager.put(key, fontRes);
			}
		}
	}

}
