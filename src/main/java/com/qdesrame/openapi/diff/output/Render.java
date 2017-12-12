package com.qdesrame.openapi.diff.output;

import com.qdesrame.openapi.diff.OpenApiDiff;

public interface Render {
	
	String render(OpenApiDiff diff);

}
