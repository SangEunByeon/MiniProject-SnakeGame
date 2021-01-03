package SnakeGame;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.LinkedList;
import java.util.Random;

public class SnakeGame {
	static class MyFrame extends JFrame {

		static class XY {
			int x;
			int y;

			public XY(int x, int y) {
				this.x = x;
				this.y = y;
			}
		}

		static JPanel panelNorth;
		static JPanel panelCenter;
		static JLabel labelTitle;
		static JLabel labelMessage;
		static JPanel[][] panels = new JPanel[20][20]; // 바둑판 모양
		static int[][] map = new int[20][20]; // fruit 9, bomb 8, 0 blank
		static LinkedList<XY> snake = new LinkedList<XY>();
		static int dir = 3; // move direction , 0:up, 1:down, 2:left, 3:right
		static int score = 0;
		static int time = 0; // game time 단위 1초
		static int timeTickCount = 0; // per 200ms
		static Timer timer = null;

		public MyFrame(String title) {
			super(title);
			this.setSize(400, 500);
			this.setVisible(true);
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			initUI();
			makeSnakeList(); // make Snake body
			startTimer();
			setKeyListener(); // listen key event
			makeFruit(); // 뱀 열매
		}

		public void makeFruit() {
			Random rand = new Random();
			// 0-19 x, 0-19 y
			int randX = rand.nextInt(19);
			int randY = rand.nextInt(19);
			map[randX][randY] = 9;
		}

		public void setKeyListener() {
			this.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_UP) {
						if (dir != 1)
							dir = 0;
					} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
						if (dir != 0)
							dir = 1;
					} else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
						if (dir != 3)
							dir = 2;
					} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
						if (dir != 2)
							dir = 3;
					}
				}
			});
		}

		public void startTimer() {
			timer = new Timer(50, new ActionListener() { // 50 ms per 1 second
				@Override
				public void actionPerformed(ActionEvent e) {
					timeTickCount += 1;

					if (timeTickCount % 20 == 0) {
						time++; // 1초 증가

						moveSnake(); // 뱀 움직이기
						updateUI(); // UI갱신
					}

				}
			});
			timer.start(); // Start Timer! 객체 만들어서 시작
		}

		public void moveSnake() {
			XY headXY = snake.get(0);
			int headX = headXY.x;
			int headY = headXY.y;

			if (dir == 0) {// 0:up, 1:down, 2:left, 3:right
				boolean isColl = checkCollision(headX, headY - 1);
				if (isColl == true) {
					labelMessage.setText("Game Over");
					timer.stop();
					return;
				}
				snake.add(0, new XY(headX, headY - 1)); // add head
				snake.remove(snake.size() - 1); // remove tail
			} else if (dir == 1) {
				boolean isColl = checkCollision(headX, headY + 1);
				if (isColl == true) {
					labelMessage.setText("Game Over");
					timer.stop();
					return;
				}
				snake.add(0, new XY(headX, headY + 1));
				snake.remove(snake.size() - 1);
			} else if (dir == 2) {
				boolean isColl = checkCollision(headX - 1, headY);
				if (isColl == true) {
					labelMessage.setText("Game Over");
					timer.stop();
					return;
				}
				snake.add(0, new XY(headX - 1, headY));
				snake.remove(snake.size() - 1);
			} else if (dir == 3) {
				boolean isColl = checkCollision(headX + 1, headY);
				if (isColl == true) {
					labelMessage.setText("Game Over");
					timer.stop();
					return;
				}
				snake.add(0, new XY(headX + 1, headY));
				snake.remove(snake.size() - 1);
			}
		}

		public boolean checkCollision(int headX, int headY) {
			if (headX < 0 || headX > 19 || headY < 0 || headY > 19) {// 벽에 부딫힌 상황
				return true;
			}
			if (map[headY][headX] == 9) { // 열매에 충돌
				map[headY][headX] = 0;
				addTail();
				makeFruit();
				score += 100;
			}

			return false;
		}

		public void addTail() {
			int tailX = snake.get(snake.size() - 1).x;
			int tailY = snake.get(snake.size() - 1).y;
			int tailX2 = snake.get(snake.size() - 2).x;
			int tailY2 = snake.get(snake.size() - 2).y;

			if (tailX < tailX2) { // to Right : attach to left
				snake.add(new XY(tailX - 1, tailY));
			} else if (tailX > tailX2) {// to Left : attach to Right
				snake.add(new XY(tailX + 1, tailY));
			} else if (tailY < tailY2) { // down
				snake.add(new XY(tailX, tailY - 1));
			} else if (tailY > tailY2) {
				snake.add(new XY(tailX, tailY + 1));
			}
		}

		public void updateUI() {
			labelTitle.setText("score: " + score + "Time: " + time);

			// clear tile(panel)
			for (int i = 0; i < 20; i++) {
				for (int j = 0; j < 20; j++) {
					if (map[i][j] == 0) {// blank
						panels[i][j].setBackground(Color.gray);
					} else if (map[i][j] == 9) {
						panels[i][j].setBackground(Color.green);
					}
				}
			}
			// draw snake
			int index = 0;
			for (XY xy : snake) {
				if (index == 0) { // head
					panels[xy.y][xy.x].setBackground(Color.RED);

				} else {// body(s), tail
					panels[xy.y][xy.x].setBackground(Color.BLUE);
				}
				index++;
			}

		}

		public void makeSnakeList() {
			snake.add(new XY(10, 10)); // head of snake
			snake.add(new XY(9, 10)); // body of snake
			snake.add(new XY(8, 10));
		}

		public void initUI() {
			this.setLayout(new BorderLayout());

			panelNorth = new JPanel();
			panelNorth.setPreferredSize(new Dimension(400, 100));
			panelNorth.setBackground(Color.black);
			panelNorth.setLayout(new FlowLayout());

			labelTitle = new JLabel("Score: 0, Time: 0sec");
			labelTitle.setPreferredSize(new Dimension(400, 50));
			labelTitle.setFont(new Font("TimesRoman", Font.BOLD, 20));
			labelTitle.setForeground(Color.white);
			labelTitle.setHorizontalAlignment(JLabel.CENTER); // 글씨 중앙 배치
			panelNorth.add(labelTitle);

			labelMessage = new JLabel("Eat Fruit!");
			labelMessage.setPreferredSize(new Dimension(400, 20));
			labelMessage.setFont(new Font("TimesRoman", Font.BOLD, 20));
			labelMessage.setForeground(Color.yellow);
			labelMessage.setHorizontalAlignment(JLabel.CENTER); // 글씨 중앙 배치
			panelNorth.add(labelMessage);

			this.add("North", panelNorth);

			panelCenter = new JPanel();
			panelCenter.setLayout(new GridLayout(20, 20)); // 이차배열 JPanel을 넣어줄 자리
			for (int i = 0; i < 20; i++) { // i Loop: Row
				for (int j = 0; j < 20; j++) { // J Loop: Column
					map[i][j] = 0; // init 0: Blank
					panels[i][j] = new JPanel();
					panels[i][j].setPreferredSize(new Dimension(20, 20));
					panels[i][j].setBackground(Color.gray);
					panelCenter.add(panels[i][j]);
				}
			}
			this.add("Center", panelCenter);
			this.pack(); // 사이즈를 조절하여 흰 공간을 없애줌, Remove empty space

			panelCenter.setPreferredSize(new Dimension());
		}

	}

	public static void main(String[] args) {
		new MyFrame("Snake Game");

	}

}
