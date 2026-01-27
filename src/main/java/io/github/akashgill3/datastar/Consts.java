package io.github.akashgill3.datastar;

import io.github.akashgill3.datastar.events.ElementPatchMode;
import io.github.akashgill3.datastar.events.Namespace;

/** Constants used by Datastar. */
public final class Consts {
  private Consts() {}

  public static final String DATASTAR_KEY = "datastar";

  public static final long DEFAULT_SSE_RETRY_DURATION_MS = 1000L;
  public static final boolean DEFAULT_ELEMENTS_USE_VIEW_TRANSITIONS = false;
  public static final boolean DEFAULT_PATCH_SIGNAL_ONLY_IF_MISSING = false;
  public static final boolean DEFAULT_EXECUTE_AUTO_REMOVE = true;
  public static final Namespace DEFAULT_NAMESPACE = Namespace.HTML;
  public static final ElementPatchMode DEFAULT_ELEMENT_PATCH_MODE = ElementPatchMode.Outer;

  public static final String SELECTOR_DATALINE_LITERAL = "selector";
  public static final String MODE_DATALINE_LITERAL = "mode";
  public static final String NAMESPACE_DATALINE_LITERAL = "namespace";
  public static final String ELEMENTS_DATALINE_LITERAL = "elements";
  public static final String USE_VIEW_TRANSITION_DATALINE_LITERAL = "useViewTransition";
  public static final String SIGNALS_DATALINE_LITERAL = "signals";
  public static final String ONLY_IF_MISSING_DATALINE_LITERAL = "onlyIfMissing";
}
