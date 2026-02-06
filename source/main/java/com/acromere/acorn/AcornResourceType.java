package com.acromere.acorn;

import com.acromere.product.Rb;
import com.acromere.xenon.RbKey;
import com.acromere.xenon.Xenon;
import com.acromere.xenon.XenonProgramProduct;
import com.acromere.xenon.resource.Resource;
import com.acromere.xenon.resource.ResourceType;
import com.acromere.xenon.resource.Codec;
import com.acromere.xenon.resource.PlaceholderCodec;

public class AcornResourceType extends ResourceType {

	private static final String URI_PATTERN = "acorn:tester";

	public static final java.net.URI URI = java.net.URI.create( URI_PATTERN );

	public AcornResourceType( XenonProgramProduct product ) {
		super( product, "acorn" );

		PlaceholderCodec codec = new PlaceholderCodec();
		codec.addSupported( Codec.Pattern.URI, URI_PATTERN );
		setDefaultCodec( codec );
	}

	@Override
	public String getKey() {
		return URI_PATTERN;
	}

	@Override
	public boolean assetOpen( Xenon program, Resource resource ) {
		resource.setUri( URI );
		resource.setName( Rb.text( RbKey.ASSET, "acorn-name") );

		// Setting the scheme when the asset is opened solves a bunch of "new" asset problems
		resource.setScheme( program.getResourceManager().getScheme( URI.getScheme() ) );

		resource.setModified( false );
		return true;
	}

}
