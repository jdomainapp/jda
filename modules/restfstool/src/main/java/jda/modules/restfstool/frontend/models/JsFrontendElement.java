package jda.modules.restfstool.frontend.models;

import jda.modules.restfstool.frontend.templates.JsTemplate;

public interface JsFrontendElement {
    JsTemplate getTemplate();
    String getAsString();
}
