/*
 * Copyright LWJGL. All rights reserved.
 * License terms: http://lwjgl.org/license.php
 */
package org.lwjgl.demo.glfw;

import org.lwjgl.LWJGLUtil;
import org.lwjgl.LWJGLUtil.Platform;
import org.lwjgl.Sys;
import org.lwjgl.demo.opengl.AbstractGears;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.system.glfw.GLFWvidmode;
import org.lwjgl.system.glfw.WindowCallback;
import org.lwjgl.system.glfw.WindowCallbackAdapter;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.glfw.GLFW.*;
import static org.lwjgl.system.macosx.CGL.*;

/** The Gears demo implemented using GLFW. */
public class Gears extends AbstractGears {

	private long      window;
	private GLContext context;

	public static void main(String[] args) {
		new Gears().execute();
	}

	@Override
	protected void init() {
		Sys.touch();

		if ( glfwInit() != GL11.GL_TRUE )
			throw new IllegalStateException("Unable to initialize glfw");

		long monitor = glfwGetPrimaryMonitor();
		ByteBuffer vidmode = glfwGetVideoMode(monitor);

		int monitorWidth = GLFWvidmode.widthGet(vidmode);
		int monitorHeight = GLFWvidmode.heightGet(vidmode);

		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GL_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);
		glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GL_TRUE);

		int WIDTH = 300;
		int HEIGHT = 300;

		window = glfwCreateWindow(WIDTH, HEIGHT, "GLFW Gears Demo", 0L, 0L);

		WindowCallback.set(window, new WindowCallbackAdapter() {
			@Override
			public void key(long window, int key, int scancode, int action, int mods) {
				if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
					glfwSetWindowShouldClose(window, GL_TRUE);
			}
		});

		glfwSetWindowPos(
			window,
			(monitorWidth - WIDTH) / 2,
			(monitorHeight - HEIGHT) / 2
		);

		glfwMakeContextCurrent(window);
		context = GLContext.createFromCurrent();

		glfwSwapInterval(1);
		glfwShowWindow(window);

		System.out.println(Thread.currentThread());
	}

	@Override
	protected void loop() {
		long lastUpdate = System.currentTimeMillis();
		int frames = 0;

		while ( glfwWindowShouldClose(window) == GL_FALSE ) {
			try {
				if ( LWJGLUtil.getPlatform() == Platform.MACOSX )
					CGLLockContext(context.getHandle());

				renderLoop();
			} finally {
				if ( LWJGLUtil.getPlatform() == Platform.MACOSX )
					CGLUnlockContext(context.getHandle());
			}

			glfwPollEvents();
			glfwSwapBuffers(window);

			frames++;

			long time = System.currentTimeMillis();
			int UPDATE_EVERY = 5; // seconds
			if ( UPDATE_EVERY * 1000L <= time - lastUpdate ) {
				lastUpdate = time;

				System.out.printf("%d frames in %d seconds = %.2f fps\n", frames, UPDATE_EVERY, (frames / (float)UPDATE_EVERY));
				frames = 0;
			}
		}

		glfwDestroyWindow(window);
	}

	@Override
	protected void destroy() {
		try {
			glfwTerminate();
		} catch (Throwable t) {
			System.err.println("CLEANUP FAILED:");
			t.printStackTrace();
		}
	}

}