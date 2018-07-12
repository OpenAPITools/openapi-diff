package com.qdesrame.openapi.diff.model;

import com.qdesrame.openapi.diff.model.schema.ChangedExtensions;
import com.qdesrame.openapi.diff.utils.ChangedUtils;
import io.swagger.v3.oas.models.security.OAuthFlow;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by adarsh.sharma on 12/01/18.
 */
@Getter
@Setter
public class ChangedOAuthFlow implements Changed {
    private OAuthFlow oldOAuthFlow;
    private OAuthFlow newOAuthFlow;

    private boolean changedAuthorizationUrl;
    private boolean changedTokenUrl;
    private boolean changedRefreshUrl;
    private ChangedExtensions changedExtensions;

    public ChangedOAuthFlow(OAuthFlow oldOAuthFlow, OAuthFlow newOAuthFlow) {
        this.oldOAuthFlow = oldOAuthFlow;
        this.newOAuthFlow = newOAuthFlow;
    }

    @Override
    public DiffResult isChanged() {
        if (!changedAuthorizationUrl && !changedTokenUrl && !changedRefreshUrl) {
            if (ChangedUtils.isUnchanged(changedExtensions)) {
                return DiffResult.NO_CHANGES;
            } else if (ChangedUtils.isCompatible(changedExtensions)) {
                return DiffResult.COMPATIBLE;
            }
        }
        return DiffResult.INCOMPATIBLE;
    }
}
