package org.sokybot.app;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.RGBImageFilter;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.extras.FlatSVGIcon.ColorFilter;
import com.formdev.flatlaf.util.GrayFilter;

@Configuration
@Lazy
public class Icons {

	@Autowired
	private ResourceLoader resourceLoader;

	@Bean
	Icon feed() throws IOException {
		FlatSVGIcon icon = new FlatSVGIcon(
				this.resourceLoader.getResource("classpath:icons/feed.svg").getFile());
		icon = icon.derive(0.40f) ; 
		ColorFilter filter = ColorFilter.getInstance();
		filter.add(Color.black, Color.DARK_GRAY, Color.LIGHT_GRAY) ;
		
		icon.setColorFilter(filter);
		return icon;
	}

}
