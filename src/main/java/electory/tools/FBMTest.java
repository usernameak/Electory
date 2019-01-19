package electory.tools;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Point;
import java.util.HashSet;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import electory.world.gen.noise.FBM;

public class FBMTest extends JFrame {

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
					FBMTest frame = new FBMTest();
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
	public FBMTest() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		float[][] colors = new float[2048][2048];

		
		Random rand = new Random();
		
		
		FBM fbm = new FBM(8, rand.nextLong());
		
		long seed1 = rand.nextLong();

		/*for (int i = 0; i < 2048; i++) {
			for (int j = 0; j < 2048; j++) {
				int ix = i / 256;
				int iy = j / 256;
				float fx = (i / 256.f - ix);
				float fy = (j / 256.f - iy);
				float m_dist = 1f;
				int m_nx = 0;
				int m_ny = 0;
				int m_nz = 0;
				float fv = (float) fbm.val(i / 64f, j / 64f);
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
								m_nz = nz;
							}
						// }
					}
				}
				long c = xorshift64star(xorshift64star(m_ny) ^ m_nx);
				colors[i][j] = c;
			}
		}*/
		
		for (int i = 0; i < 2048; i++) {
			for (int j = 0; j < 2048; j++) {
				colors[i][j] = (float) fbm.val(i / 256f, j / 256f);
			}
		}

		JPanel panel = new JPanel() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);

				g.setColor(Color.BLACK);

				for (int i = 0; i < 2048; i++) {
					for (int j = 0; j < 2048; j++) {
						g.setColor(new Color(colors[i][j], colors[i][j], colors[i][j]));
						g.fillRect(i, j, 1, 1);
					}
				}
			}
		};
		contentPane.add(panel, BorderLayout.CENTER);
	}

}
