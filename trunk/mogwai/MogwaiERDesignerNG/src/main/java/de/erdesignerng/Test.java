package de.erdesignerng;

import javax.swing.JFrame;

import de.erdesignerng.visual.editor.view.ViewEditorView;

public class Test extends JFrame {
    
    public Test() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(new ViewEditorView());
    }
    
    public static void main(String[] args) {
        Test test = new Test();
        test.pack();
        test.setVisible(true);
    }
}