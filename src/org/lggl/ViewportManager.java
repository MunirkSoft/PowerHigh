package org.lggl;

import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;

import org.lggl.graphics.Window;

public abstract class ViewportManager {

	protected Map<String, Object> props = new HashMap<>();
	
	public abstract Rectangle getViewport(Window win);
	
	public Map<String, Object> getSpecialProperties() {
		return props;
	}

}
