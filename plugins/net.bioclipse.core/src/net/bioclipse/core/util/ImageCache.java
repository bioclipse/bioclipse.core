/*******************************************************************************
 * Copyright (c) 2008 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Ola Spjuth
 *     
 ******************************************************************************/
package net.bioclipse.core.util;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
/**
 * Cache images to save memory and time
 * 
 * @author ola
 *
 */
public class ImageCache {
    private final Map<ImageDescriptor, Image> imageMap;
    public ImageCache() {
        super();
        imageMap = new HashMap<ImageDescriptor, Image>();    
    }
    @SuppressWarnings("unchecked")
    public Image getImage(ImageDescriptor imageDescriptor) {
        if (imageDescriptor == null)
            return null;
        Image image = (Image) imageMap.get(imageDescriptor);
        if (image == null) {
            image = imageDescriptor.createImage();
            imageMap.put(imageDescriptor, image);
        }
        return image;
    }
    public void dispose() {
        for (Image img : imageMap.values()){
            img.dispose();
        }
        imageMap.clear();
    }
}
