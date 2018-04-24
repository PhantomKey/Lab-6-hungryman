package com.thehungryguy.ui;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

public class LoaderBar extends Canvas {
	private static final double BAR_WIDTH = 300;
	private static final double BAR_HEIGHT = 10;
	private boolean isAnimating = false;

	public LoaderBar() {
		super(BAR_WIDTH, BAR_HEIGHT);
	}

	public void startAnimation() {
		if (isAnimating) {
			return;
		}
		
		

		GraphicsContext ctx = getGraphicsContext2D();
		System.out.println("hi");
		AnimationTimer animationTimer = new AnimationTimer() {
			long initialNow = 0;
			boolean isFirst = true;
			

			@Override
			public void handle(long now) {
				if (isFirst) {
					isFirst = false;
					initialNow = now;
				}

				ctx.clearRect(0, 0, BAR_WIDTH, BAR_HEIGHT);

				if (!isAnimating) {
					stop();
					return;
				}

				long ellapsed = now - initialNow;

				double period = 0.5e9;
				System.out.println("kuykuykyu");
				double progress = (ellapsed / period) % 1.0;
				double l = progress > 0.5 ? Math.pow(Math.sin((progress * 4 - 3) * Math.PI / 2.0) / 2 + 0.5, 1.5) : 0;
				double r = progress < 0.5 ? Math.pow(Math.sin((progress * 4 - 1) * Math.PI / 2.0) / 2 + 0.5, 1.5) : 1;

				boolean isPrimaryColor = (ellapsed / period) % 2.0 < 1;
				ctx.setFill(isPrimaryColor ? ApplicationColors.primaryColor : ApplicationColors.secondaryColor);
				ctx.fillRect(l * BAR_WIDTH, 0, (r - l) * BAR_WIDTH, BAR_HEIGHT);
			}
		};
		
		
		animationTimer.start();
		System.out.println("is running");
		

		isAnimating = true;
	}

	public void stopAnimation() {
		if (!isAnimating) {
			return;
		}

		isAnimating = false;
	}
}
