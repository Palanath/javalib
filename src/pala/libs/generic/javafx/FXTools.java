package pala.libs.generic.javafx;

import static javafx.scene.paint.Color.BLUE;
import static javafx.scene.paint.Color.GOLD;
import static javafx.scene.paint.Color.GREEN;
import static javafx.scene.paint.Color.RED;

import java.awt.MouseInfo;
import java.util.LinkedList;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.FillTransition;
import javafx.animation.Interpolator;
import javafx.animation.SequentialTransition;
import javafx.animation.Transition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.beans.binding.NumberExpression;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;

public final class FXTools {
	public static final Color DEFAULT_WINDOW_COLOR = new Color(0.34, 0.34, 0.34, 1),
			DEFAULT_NODE_BACKGROUND_COLOR = DEFAULT_WINDOW_COLOR.interpolate(Color.BLACK, 0.25),
			DEFAULT_NODE_BORDER_COLOR = Color.BLACK, ITEM_BORDER_COLOR = Color.BLUE,
			SECONDARY_WINDOW_BORDER_COLOR = ITEM_BORDER_COLOR.interpolate(DEFAULT_WINDOW_COLOR, 0.5);

	public static final Border DEFAULT_NODE_BORDER = FXTools.getBorderFromColor(DEFAULT_NODE_BORDER_COLOR);
	public static final Background DEFAULT_NODE_BACKGROUND = FXTools
			.getBackgroundFromColor(DEFAULT_NODE_BACKGROUND_COLOR);
	public static final double COMMON_BORDER_WIDTH = 2;

	private static final Color[] DEFAULT_COLORWHEEL_TRANSITION_COLORS = { RED, GOLD, GREEN, BLUE };

	private static final Duration DEFAULT_COLORWHEEL_TRANSITION_DURATION = Duration.seconds(0.5);

	private static Paint[] inputBorderColors, inputActivatedColors;

	private static final Object TABLE_COLUMN_RELATIVE_WIDTH_KEY = new Object(),
			TABLE_VIEW_TOTAL_COLUMN_PROPORTION_KEY = new Object();

	public static void addHoverNode(final Node node, final Node hoverNode, final Window stage) {

		new Object() {
			private final Popup popup = new Popup();

			{
				popup.getScene().setRoot(new AnchorPane(hoverNode));

				node.addEventHandler(MouseEvent.MOUSE_MOVED, event -> {
					popup.setX(event.getScreenX());
					popup.setY(event.getScreenY() - 50);
					popup.show(stage);
					popup.sizeToScene();
				});
				node.addEventHandler(MouseEvent.MOUSE_EXITED, event -> popup.hide());
			}
		};

	}

	public static Label addHoverText(final Node node, final Label text, final Window stage) {
		addHoverNode(node, text, stage);
		return text;
	}

	public static Label addHoverText(final Node node, final String text, final Color backgroundColor,
			final Window stage) {
		final Label label = addHoverText(node, text, stage);
		label.getScene().setFill(backgroundColor);
		return label;
	}

	public static Label addHoverText(final Node node, final String text, final Window stage) {
		return addHoverText(node, new Label(text), stage);
	}

	public static void addPopup(final Node node, final Parent popupRoot, final Window stage) {
		new Object() {
			private final Popup popup = new Popup();

			{
				popup.getScene().setRoot(popupRoot);

				node.addEventHandler(MouseEvent.MOUSE_MOVED, event -> {
					popup.setX(event.getScreenX());
					popup.setY(event.getScreenY() - 50);
					if (!popup.isShowing())
						popup.show(stage);
					popup.sizeToScene();
				});
				node.addEventHandler(MouseEvent.MOUSE_EXITED, event -> popup.hide());
			}
		};
	}

	public static Transition applyHoverColorAnimation(final Shape shape) {
		return applyHoverColorAnimation(shape, DEFAULT_COLORWHEEL_TRANSITION_DURATION,
				DEFAULT_COLORWHEEL_TRANSITION_COLORS);
	}

	public static Transition applyHoverColorAnimation(final Shape shape, final Color... colors) {
		return applyHoverColorAnimation(shape, DEFAULT_COLORWHEEL_TRANSITION_DURATION, colors);
	}

	public static Transition applyHoverColorAnimation(final Shape shape, final Duration duration,
			final Color... colors) {

		final Transition repeater = buildColorwheelTransition(shape, duration, colors);

		shape.setOnMouseEntered(event -> repeater.play());
		shape.setOnMouseExited(event -> repeater.pause());

		return repeater;
	}

	public static Transition buildColorwheelTransition(final Shape shape) {
		return buildColorwheelTransition(shape, DEFAULT_COLORWHEEL_TRANSITION_COLORS);
	}

	public static Transition buildColorwheelTransition(final Shape shape, final Color... colors) {
		return buildColorwheelTransition(shape, DEFAULT_COLORWHEEL_TRANSITION_DURATION, colors);
	}

	public static Transition buildColorwheelTransition(final Shape shape, final Duration duration) {
		return buildColorwheelTransition(shape, duration, DEFAULT_COLORWHEEL_TRANSITION_COLORS);
	}

	public static Transition buildColorwheelTransition(final Shape shape, final Duration duration,
			final Color... colors) {
		shape.setFill(colors[0]);
		final FillTransition[] transitions = new FillTransition[colors.length];
		for (int i = 0; i < colors.length - 1;)
			transitions[i] = new FillTransition(duration, shape, colors[i], colors[++i]);

		transitions[transitions.length - 1] = new FillTransition(duration, shape, colors[colors.length - 1], colors[0]);

		final SequentialTransition repeater = new SequentialTransition(transitions);

		repeater.setCycleCount(Animation.INDEFINITE);
		return repeater;
	}

	public static void clearScrollPaneBackground(final ScrollPane... scrollPanes) {
		for (final ScrollPane sp : scrollPanes) {
			sp.getStylesheets().add("/branch/alixia/kröw/unnamed/tools/default-background.css");
			sp.getStyleClass().add("default-background");
		}
	}

	public static Background getBackgroundFromColor(final Paint color) {
		return new Background(new BackgroundFill(color, null, null));
	}

	public static Background getBackgroundFromColor(final Paint color, final double radius) {
		return new Background(new BackgroundFill(color, radius < 0 ? null : new CornerRadii(radius), null));
	}

	public static Border getBorderFromColor(final Paint color) {
		return getBorderFromColor(color, 2);
	}

	public static Border getBorderFromColor(final Paint color, final double width) {
		return new Border(
				new BorderStroke(color, BorderStrokeStyle.SOLID, null, width < 0 ? null : new BorderWidths(width)));
	}

	public static Border getBorderFromColor(final Paint color, final double width, final double radii) {
		return new Border(new BorderStroke(color, BorderStrokeStyle.SOLID, radii < 0 ? null : new CornerRadii(radii),
				width < 0 ? null : new BorderWidths(width)));
	}

	public static void setAllAnchors(final double top, final double right, final double bottom, final double left,
			final Node... nodes) {
		for (final Node n : nodes) {
			AnchorPane.setTopAnchor(n, top);
			AnchorPane.setRightAnchor(n, right);
			AnchorPane.setBottomAnchor(n, bottom);
			AnchorPane.setLeftAnchor(n, left);
		}

	}

	public static void setAllAnchors(final double anchorDistance, final Node... nodes) {
		setAllAnchors(anchorDistance, anchorDistance, anchorDistance, anchorDistance, nodes);
	}

	public static void setDefaultBackground(final Region item) {
		item.setBackground(getBackgroundFromColor(DEFAULT_WINDOW_COLOR));
		if (item instanceof ScrollPane) {
			item.getStylesheets().add("branch/alixia/kröw/unnamed/tools/default-background.css");
			item.getStyleClass().add("default-background");
		}
	}

	public static void setInputActivatedColors(final Paint[] inputActivatedColors) {
		if (inputActivatedColors != null && inputActivatedColors.length == 0)
			return;
		FXTools.inputActivatedColors = inputActivatedColors;
	}

	public static void setInputBorderColors(final Paint[] inputBorderColors) {
		if (inputBorderColors != null && inputBorderColors.length == 0)
			return;
		FXTools.inputBorderColors = inputBorderColors;
	}

	/**
	 * Allows the user to drag the given {@link Node} to move the given
	 * {@link javafx.stage.Window}.
	 *
	 * @param window The {@link javafx.stage.Window} that will be moved when the
	 *               {@link Node} is dragged.
	 * @param node   The {@link javafx.stage.Window} that the user will drag to move
	 *               the given {@link Stage}.
	 */
	public static void setPaneDraggableByNode(final javafx.stage.Window window, final Node node) {
		new Object() {

			private double xOffset, yOffset;

			{
				node.setOnMousePressed(event -> {
					xOffset = window.getX() - event.getScreenX();
					yOffset = window.getY() - event.getScreenY();
				});

				node.setOnMouseDragged(event -> {
					window.setX(event.getScreenX() + xOffset);
					window.setY(event.getScreenY() + yOffset);
				});
			}

		};
	}

	/**
	 * <p>
	 * Sets this application's {@link Application#stage} as draggable by the
	 * specified {@link Node}.
	 * <p>
	 * The {@link Node#setOnMousePressed(javafx.event.EventHandler)} and
	 * {@link Node#setOnMouseDragged(javafx.event.EventHandler)} methods are called
	 * on the given {@link Node} to allow the current {@link Application#stage}
	 * object to be moved via the user dragging the given {@link Node}.
	 *
	 * @param node The {@link Node} that will be used to move the WindowManager.
	 */
	public static void setPaneDraggableByNode(final Node node, final Stage stage) {
		/**
		 * This object is made so that the <code>xOffset</code> and <code>yOffset</code>
		 * variables can be used inside the lambda expressions without being made final.
		 *
		 * @author Palanath
		 *
		 */
		new Object() {

			private double xOffset, yOffset;

			{
				node.setOnMousePressed(event -> {
					xOffset = stage.getX() - event.getScreenX();
					yOffset = stage.getY() - event.getScreenY();
				});

				node.setOnMouseDragged(event -> {
					stage.setX(event.getScreenX() + xOffset);
					stage.setY(event.getScreenY() + yOffset);
				});
			}

		};
	}

	public static void setTableColumnRelativeWidth(final double portion, final TableColumn<?, ?> column) {
		column.getProperties().put(TABLE_COLUMN_RELATIVE_WIDTH_KEY, portion);
	}

	public static void sizeTableViewColumns(final TableView<?> tableView) {

		if (tableView.getProperties().containsKey(TABLE_VIEW_TOTAL_COLUMN_PROPORTION_KEY))
			throw new IllegalArgumentException();

		final int count = tableView.getColumns().size();
		final DoubleProperty totalProportion = new SimpleDoubleProperty();
		tableView.getProperties().put(TABLE_VIEW_TOTAL_COLUMN_PROPORTION_KEY, totalProportion);
		for (final TableColumn<?, ?> tc : tableView.getColumns()) {
			final double value = tc.getProperties().containsKey(TABLE_COLUMN_RELATIVE_WIDTH_KEY)
					? (double) tc.getProperties().get(TABLE_COLUMN_RELATIVE_WIDTH_KEY)
					: 1d / count;

			// TODO Fix
			totalProportion.set(totalProportion.get() + value);
			tc.prefWidthProperty().bind(tableView.widthProperty().divide(totalProportion).multiply(value));
		}
	}

	public static void sizeTableViewColumns(final TableView<?> tableView, final NumberExpression totalSize) {

		if (tableView.getProperties().containsKey(TABLE_VIEW_TOTAL_COLUMN_PROPORTION_KEY))
			throw new IllegalArgumentException();

		final int count = tableView.getColumns().size();
		final DoubleProperty totalProportion = new SimpleDoubleProperty();
		tableView.getProperties().put(TABLE_VIEW_TOTAL_COLUMN_PROPORTION_KEY, totalProportion);
		for (final TableColumn<?, ?> tc : tableView.getColumns()) {
			final double value = tc.getProperties().containsKey(TABLE_COLUMN_RELATIVE_WIDTH_KEY)
					? (double) tc.getProperties().get(TABLE_COLUMN_RELATIVE_WIDTH_KEY)
					: 1d / count;

			// TODO Fix
			totalProportion.set(totalProportion.get() + value);
			tc.prefWidthProperty().bind(totalSize.divide(totalProportion).multiply(value));
		}
	}

	/**
	 * Spawns a floating piece of text that flies upwards a little then disappears.
	 * The source point of the text is specified via the {@code x} and {@code y}
	 * parameters.
	 *
	 * @param text  The text to render.
	 * @param color The color of the rendered text.
	 * @param x     The starting x position of the text.
	 * @param y     The starting y position of the text.
	 */
	public static void spawnLabel(final String text, final Color color, final double x, final double y,
			final Window stage) {
		final Popup pc = new Popup();
		final Label label = new Label(text);
		label.setMouseTransparent(true);
		final TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(2), label);
		final FadeTransition opacityTransition = new FadeTransition(Duration.seconds(2), label);

		pc.getScene().setRoot(label);
		/* Style label */
		label.setTextFill(color);
		label.setBackground(null);
		final double fontSize = 16;
		label.setStyle("-fx-font-weight: bold; -fx-font-size: " + fontSize + "px;");
		/* Set Popup positions */
		pc.setX(x);
		pc.setWidth(label.getMaxWidth());
		pc.setY(y - 50);
		/* Build transitions */
		translateTransition.setFromY(30);
		translateTransition.setFromX(0);
		translateTransition.setToX(0);
		translateTransition.setToY(5);
		translateTransition.setInterpolator(Interpolator.EASE_OUT);
		opacityTransition.setFromValue(0.7);
		opacityTransition.setToValue(0.0);
		opacityTransition.setOnFinished(e -> pc.hide());
		/* Show the Popup */
		pc.show(stage);
		pc.setHeight(50);
		/* Play the transitions */
		translateTransition.play();
		opacityTransition.play();
	}

	public static void spawnLabelAtMousePos(final String text, final Color color, final Window stage) {
		spawnLabel(text, color, MouseInfo.getPointerInfo().getLocation().getX(),
				MouseInfo.getPointerInfo().getLocation().getY(), stage);
	}

	public static void styleBasicInput(final Paint borderColor, final Paint activatedBorderColor,
			final Region... inputs) {
		for (final Region r : inputs) {
			r.setBackground(getBackgroundFromColor(DEFAULT_NODE_BACKGROUND_COLOR));
			r.setBorder(getBorderFromColor(borderColor));
			r.getStylesheets().add("branch/alixia/kröw/unnamed/tools/basic-input.css");
			r.getStyleClass().add("basic-input");

			(r instanceof Button ? ((Button) r).armedProperty() : r.focusedProperty())
					.addListener((ChangeListener<Boolean>) (observable, oldValue, newValue) -> r
							.setBorder(getBorderFromColor(newValue ? activatedBorderColor : borderColor)));

		}
	}

	public static void styleBasicInput(final Region... inputs) {
		final LinkedList<Button> buttons = new LinkedList<>();
		final LinkedList<Region> others = new LinkedList<>();
		for (final Region r : inputs)
			if (r instanceof Button)
				buttons.add((Button) r);
			else
				others.add(r);

		styleBasicInput(ITEM_BORDER_COLOR, Color.RED, buttons.toArray(new Button[0]));
		styleBasicInput(ITEM_BORDER_COLOR, Color.GREEN, others.toArray(new Region[0]));

	}

	public static void styleInputs(final double fontSize, final Region... inputs) {

		if (inputBorderColors == null && inputActivatedColors == null)
			styleBasicInput(inputs);
		else
			for (final Region r : inputs) {
				if (r instanceof Labeled) {
					final Labeled labeled = (Labeled) r;
					labeled.setTextFill(Color.WHITE);
					labeled.setFont(Font.font("Courier", FontWeight.BOLD, fontSize));
				}
				r.setBackground(FXTools.getBackgroundFromColor(DEFAULT_NODE_BACKGROUND_COLOR));
				r.setBorder(FXTools.getBorderFromColor(inputBorderColors == null ? Color.BLACK
						: inputBorderColors[(int) (Math.random() * inputBorderColors.length)]));

				(r instanceof Button ? ((Button) r).armedProperty() : r.focusedProperty())
						.addListener((ChangeListener<Boolean>) (observable, oldValue, newValue) -> r.setBorder(

								getBorderFromColor(newValue
										? inputActivatedColors == null ? Color.BLACK
												: inputActivatedColors[(int) (Math.random()
														* inputActivatedColors.length)]
										: inputBorderColors == null ? Color.BLACK
												: inputBorderColors[(int) (Math.random()
														* inputBorderColors.length)])));
			}

	}

	public static void styleInputs(final Paint color, final Paint activatedColor, final double fontSize,
			final Region... inputs) {
		for (final Region r : inputs) {
			if (r instanceof Labeled) {
				final Labeled labeled = (Labeled) r;
				labeled.setTextFill(Color.WHITE);
				labeled.setFont(Font.font("Courier", FontWeight.BOLD, fontSize));
			}
			r.setBackground(FXTools.getBackgroundFromColor(DEFAULT_NODE_BACKGROUND_COLOR));
			r.setBorder(FXTools.getBorderFromColor(color));

			(r instanceof Button ? ((Button) r).armedProperty() : r.focusedProperty())
					.addListener((ChangeListener<Boolean>) (observable, oldValue, newValue) -> {
						r.setBorder(FXTools.getBorderFromColor(newValue ? activatedColor : color));
					});

		}
	}

	public static void styleInputs(final Region... inputs) {
		styleInputs(-1, inputs);
	}

	private FXTools() {
	}

}
