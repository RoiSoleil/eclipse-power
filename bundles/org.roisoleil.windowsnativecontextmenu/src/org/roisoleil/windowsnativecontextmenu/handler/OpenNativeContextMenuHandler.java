package org.roisoleil.windowsnativecontextmenu.handler;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.roisoleil.windowsnativecontextmenu.Activator;

public class OpenNativeContextMenuHandler extends AbstractHandler {

	private static final String RUNMENU_FILE_NAME = "runmenu.exe";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			File runMenu = getRunMenuFile();
			IStructuredSelection structuredSelection = (IStructuredSelection) HandlerUtil.getCurrentSelection(event);
			Object[] objects = structuredSelection.toArray();
			List<IResource> resources = new ArrayList<>();
			for (Object object : objects) {
				resources.add(Platform.getAdapterManager().getAdapter(object, IResource.class));
			}
			List<String> args = new ArrayList<>();
			args.add(runMenu.getAbsolutePath());
			args.add("/show");
			for (IResource resource : resources) {
				args.add(resource.getLocation().toFile().getAbsolutePath());
			}
			Runtime.getRuntime().exec(args.toArray(new String[0]));
		} catch (Exception exception) {
			Activator.getDefault().getLog().error("Error when calling \"" + RUNMENU_FILE_NAME + "\" !", exception);
		}
		return null;
	}

	private File getRunMenuFile() throws IOException {
		String tempDirectory = System.getProperty("java.io.tmpdir");
		File runMenuFile = new File(tempDirectory, RUNMENU_FILE_NAME);
		if (!runMenuFile.exists()) {
			try (InputStream inputStream = new BufferedInputStream(
					OpenNativeContextMenuHandler.class.getResourceAsStream(RUNMENU_FILE_NAME))) {
				try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(runMenuFile))) {
					byte[] buffer = new byte[1024];
					int len;
					while ((len = inputStream.read(buffer)) != -1) {
						outputStream.write(buffer, 0, len);
					}
				}
			}
		}
		return runMenuFile;
	}

}
