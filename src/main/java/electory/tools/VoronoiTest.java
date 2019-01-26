package electory.tools;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;

import electory.world.gen.noise.FBM;

public class VoronoiTest extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					VoronoiTest frame = new VoronoiTest();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private long xorshift64star(long x) {
		x ^= x >> 12; // a
		x ^= x << 25; // b
		x ^= x >> 27; // c
		return (x * 0x2545F4914F6CDD1DL) ^ 0x132485746548574EL;
	}

	private long xorshift64star_(long x) {
		x ^= x >> 12; // a
		x ^= x << 25; // b
		x ^= x >> 27; // c
		return x;
	}

	/**
	 * Create the frame.
	 */
	public VoronoiTest() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		float[][] colors = new float[2048][2048];

		Random rand = new Random();

		FBM fbm = new FBM(2, rand.nextLong());
		FBM fbm2 = new FBM(8, rand.nextLong());

		long seed1 = rand.nextLong();

		for (int i = 0; i < 2048; i++) {
			for (int j = 0; j < 2048; j++) {
				int ix = i / 16;
				int iy = j / 16;
				float fx = (i / 16.f - ix);
				float fy = (j / 16.f - iy);
				float m_dist = 666f;
				int m_nx = 0;
				int m_ny = 0;
				// float m_f2v = 0;
				// int m_nz = 0;
				float fv = (float) fbm.val(i / 4f, j / 4f);
				for (int y = -1; y <= 1; y++) {
					for (int x = -1; x <= 1; x++) {
						// for (int k = 0; k < 16; k++) {

						int nx = ix + x;
						int ny = iy + y;

						long s = xorshift64star(seed1);
						long t = xorshift64star(s ^ ny);
						long u = xorshift64star(t ^ nx);
						long v = xorshift64star_(t ^ nx);
						long f = xorshift64star_(v);
						int nz = ((int) xorshift64star(f)) & 0xFF;
						// long w = xorshift64star(u ^ k);
						long a = xorshift64star(v);
						long b = a & 0x001FFFFFFFFFFFFFL;
						double c = b / 9007199254740992.0;
						long d = u & 0x001FFFFFFFFFFFFFL;
						double e = d / 9007199254740992.0;
						float rx = (float) c;
						float ry = (float) e;
						float dx = x + rx - fx;
						float dy = y + ry - fy;
						float dz = (float) ((nz / 255f) - fv);
						float dist = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
						if (dist < m_dist) {
							m_dist = dist;
							m_nx = nx;
							m_ny = ny;
							// m_nz = nz;
						}
						// }
					}
				}
				// long c = xorshift64star(xorshift64star(m_ny) ^ m_nx);
				colors[i][j] = (float) fbm2.val(m_nx / 32f, m_ny / 32f);
			}
		}

		/*
		 * for (int i = 0; i < 2048; i++) { for (int j = 0; j < 2048; j++) {
		 * colors[i][j] = (float) Math.pow(fbm.val(i / 256f, j / 256f), 1.5f); } }
		 */

		// final Color[] color_lookup = new Color[] { new Color(0xFF00FF00), new
		// Color(0xFF0000FF) };

		JPanel panel = new JPanel() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);

				g.setColor(Color.BLACK);

				for (int i = 0; i < 2048; i++) {
					for (int j = 0; j < 2048; j++) {
						Color color = mixColors(Color.YELLOW, Color.BLUE, colors[i][j]);
						g.setColor(color);
						g.fillRect(i, j, 1, 1);
					}
				}
			}
		};
		contentPane.add(panel, BorderLayout.CENTER);
	}

	public Color mixColors(Color color1, Color color2, float percent) {
		double inverse_percent = 1.0 - percent;
		int redPart = (int) (color1.getRed() * percent + color2.getRed() * inverse_percent);
		int greenPart = (int) (color1.getGreen() * percent + color2.getGreen() * inverse_percent);
		int bluePart = (int) (color1.getBlue() * percent + color2.getBlue() * inverse_percent);
		return new Color(redPart, greenPart, bluePart);
	}
}
