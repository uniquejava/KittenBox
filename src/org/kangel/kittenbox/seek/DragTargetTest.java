package org.kangel.kittenbox.seek;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

public class DragTargetTest {

    public static void main(String[] args) {
        Display display = new Display();
        Shell shell = new Shell(display);
        final Table table = new Table(shell, SWT.BORDER);
        table.setSize(200, 200);

        for (int i = 0; i < 4; i++) {
            TableItem ti = new TableItem(table, SWT.NONE);
            ti.setText("drop a file here ");
        }

        // 上面是准备界面

        int operations = DND.DROP_DEFAULT | DND.DROP_COPY;

        DropTarget dropTarget = new DropTarget(table, operations);
        dropTarget.setTransfer(new Transfer[] { FileTransfer.getInstance() });
        dropTarget.addDropListener(new DropTargetListener() {

            public void dragEnter(DropTargetEvent arg0) {
            }

            public void dragLeave(DropTargetEvent arg0) {

            }

            public void dragOperationChanged(DropTargetEvent arg0) {
                // 这里可以做拖放同时按Ctrl之类的控制键改变操作的处理
            }

            public void dragOver(DropTargetEvent arg0) {
                arg0.detail = DND.DROP_COPY;

                // 这里比较重要，detail原值为DND.DROP_NONE改变detail才能让drop事件进行下去
            }

            public void drop(DropTargetEvent arg0) {
                System.out.println("drop");
                TableItem item = (TableItem) arg0.item;
                System.out.println(item == null);
                if (item == null) {
                    arg0.detail = DND.DROP_NONE;
                    return;
                }

                if (FileTransfer.getInstance().isSupportedType(arg0.currentDataType)) {
                    String[] files = (String[]) arg0.data;
                    if (files != null && files.length > 0) {
                        System.out.println(files[0]);
                        item.setText(files[0]);// 设置表格内容为文件名全路径
                    }
                }
            }

            public void dropAccept(DropTargetEvent arg0) {
                // 最后一次取消drop操作的机会
                System.out.println("dragAccept");
            }
        });

        shell.setSize(400, 400);
        shell.open();

        while (!shell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }
        display.dispose();
    }

}
