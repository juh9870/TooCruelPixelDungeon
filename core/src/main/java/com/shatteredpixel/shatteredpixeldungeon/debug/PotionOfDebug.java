package com.shatteredpixel.shatteredpixeldungeon.debug;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.ExoticPotion;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ExoticScroll;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.ui.WndTextInput;
import com.shatteredpixel.shatteredpixeldungeon.utils.Currency;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndUseItem;
import com.watabou.noosa.Image;
import com.watabou.utils.Callback;
import com.watabou.utils.ListUtils;
import com.watabou.utils.Reflection;
import com.watabou.utils.function.Consumer;
import com.watabou.utils.function.Lazy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class PotionOfDebug extends Potion {
	private ItemsCategory[] categories;

	{
		image = ItemSpriteSheet.POTION_HOLDER;
	}

	@Override
	public String name() {
		return "Potion of Debug";
	}

	@Override
	public String desc() {
		return "Gulp this down to get so high, that you can imagine any item you want, and it will appear in your hands!";
	}

	@Override
	public boolean isIdentified() {
		return true;
	}

	@Override
	public boolean isKnown() {
		return true;
	}

	@Override
	protected void drink( Hero hero ) {
		init();
		GameScene.show( new WndBetterOptions( "Pick category", "Pick item category", categoryNames() ) {
			@Override
			protected void onSelect( int index ) {
				ItemsCategory category = categories[index];
				GameScene.show( new WndBetterOptions( "Pick item", "Pick item to get", category.listItems() ) {
					@Override
					protected void onSelect( int index ) {
						Item item = category.items[index].copy();
						itemPicked( item );
					}

					@Override
					protected boolean hasIcon( int index ) {
						return true;
					}

					@Override
					protected Image getIcon( int index ) {
						return new ItemSprite( category.items[index].item().image() );
					}
				} );
			}
		} );
	}

	private void itemPicked( Item item ) {
		askAmount( item, ( i2 ) -> askUpgradeLevel( i2, ( i3 ) -> askEnchant( i3, this::giveItem ) ) );
	}

	private void askAmount( Item item, Consumer<Item> next ) {
		if ( !item.stackable ) {
			next.accept( item );
			return;
		}
		GameScene.show( new WndTextInput( "Input amount", "", 9, false, "Done", "Cancel" ) {
			@Override
			public void onSelect( boolean positive, String text ) {
				int amount = 1;
				try {
					amount = Integer.parseInt( text );
				} catch ( NumberFormatException e ) {
					GLog.w( "Invalid number" );
				}
				item.quantity( amount );
				next.accept( item );
			}
		} );
	}

	private void askUpgradeLevel( Item item, Consumer<Item> next ) {
		if ( !item.isUpgradable() ) {
			next.accept( item );
			return;
		}
		GameScene.show( new WndTextInput( "Input upgrade level", "", 6, false, "Done", "Cancel" ) {
			@Override
			public void onSelect( boolean positive, String text ) {
				int level = 0;
				try {
					if ( positive ) {
						level = Integer.parseInt( text );
					}
				} catch ( NumberFormatException e ) {
					GLog.w( "Invalid number" );
				}
				item.upgrade( level );
				next.accept( item );
			}
		} );
	}

	private void askEnchant( Item item, Consumer<Item> next ) {
		if ( !(item instanceof Weapon) ) {
			next.accept( item );
			return;
		}
		GameScene.show( new WndBetterOptions( "Choose enchant", "Pick category", "Normal", "Curses", "Better curses", "None" ) {
			@Override
			protected void onSelect( int index ) {
				Class<?>[] enchants;
				if ( index == 0 ) enchants = Weapon.Enchantment.allEnchants();
				else if ( index == 1 ) enchants = Weapon.Enchantment.getCurses();
				else if ( index == 2 ) enchants = Weapon.Enchantment.getBetterCurses();
				else {
					next.accept( item );
					return;
				}
				GameScene.show( new WndBetterOptions( "Choose enchant", "Pick an enchantment",
						ListUtils.map( enchants, String.class, Class::getSimpleName ) ) {
					@Override
					protected void onSelect( int index ) {
						((Weapon) item).enchant( (Weapon.Enchantment) Reflection.newInstance( enchants[index] ) );
						next.accept( item );
					}
				} );
			}
		} );
	}

	private void giveItem( Item item ) {
		item.identify().doPickUp( curUser );
	}

	private String[] categoryNames() {
		return ListUtils.map( categories, String.class, ( c ) -> c.name );
	}

	//region data handling

	private void init() {
		if ( categories != null ) return;
		ArrayList<ItemsCategory> cats = new ArrayList<>();
		for (Generator.Category generatorCategory : Generator.Category.values()) {
			if ( generatorCategory.classes.length == 0 ) continue;
			ItemsCategory category = new ItemsCategory( generatorCategory );
			cats.add( category );
			if ( generatorCategory == Generator.Category.POTION ) {
				cats.add( ItemsCategory.exoticPotions( category ) );
			} else if ( generatorCategory == Generator.Category.SCROLL ) {
				cats.add( ItemsCategory.exoticScrolls( category ) );
			}
		}

		categories = cats.toArray( new ItemsCategory[0] );
	}


	public static class ItemsCategory {
		public final ItemClass[] items;
		public final String name;

		public ItemsCategory( Generator.Category category ) {
			if ( category == Generator.Category.GOLD ) {
				name = "currency";
				items = new ItemClass[Currency.values().length];
				Currency[] values = Currency.values();
				for (int i = 0; i < values.length; i++) {
					items[i] = new ItemClass( values[i].item() );
				}

			} else {
				items = new ItemClass[category.classes.length];
				for (int i = 0; i < category.classes.length; i++) {
					items[i] = new ItemClass( (Class<? extends Item>) category.classes[i] );
				}
				name = category.name().toLowerCase( Locale.ROOT );
			}
		}

		public ItemsCategory( ItemClass[] items, String name ) {
			this.items = Arrays.copyOf( items, items.length );
			this.name = name;
		}

		public static ItemsCategory exoticPotions( ItemsCategory potions ) {
			ArrayList<ItemClass> items = new ArrayList<>();
			for (ItemClass item : potions.items) {
				if ( item.item() instanceof Potion ) {
					Class<? extends ExoticPotion> potion = ExoticPotion.regToExo.get( item.item().getClass() );
					items.add( new ItemClass( potion ) );
				}
			}
			return new ItemsCategory( items.toArray( new ItemClass[0] ), "exotic potion" );
		}

		public static ItemsCategory exoticScrolls( ItemsCategory scrolld ) {
			ArrayList<ItemClass> items = new ArrayList<>();
			for (ItemClass item : scrolld.items) {
				if ( item.item() instanceof Scroll ) {
					Class<? extends ExoticScroll> potion = ExoticScroll.regToExo.get( item.item().getClass() );
					items.add( new ItemClass( potion ) );
				}
			}
			return new ItemsCategory( items.toArray( new ItemClass[0] ), "exotic scrolls" );
		}

		public String[] listItems() {
			return ListUtils.map( items, String.class, ( c ) -> c.item().name() );
		}
	}


	public static class ItemClass {
		private final Lazy<Item> item;

		public ItemClass( Class<? extends Item> item ) {
			this.item = Lazy.of( () -> processItem( Reflection.newInstance( item ) ) );
		}

		public ItemClass( Item item ) {
			this.item = Lazy.of( () -> processItem( item ) );
		}

		private static Item processItem( Item item ) {
			if ( item instanceof Potion ) {
				((Potion) item).anonymize();
			} else if ( item instanceof Scroll ) {
				((Scroll) item).anonymize();
			} else if ( item instanceof Ring ) {
				((Ring) item).anonymize();
			}
			return item;
		}

		public Item item() {
			return item.get();
		}

		public Item copy() {
			return Reflection.newInstance( item.get().getClass() );
		}
	}
	//endregion


	public static class WndBetterOptions extends Window {

		private static final int WIDTH_P = 120;
		private static final int WIDTH_L = 144;

		private static float maxWidth() {
			return PixelScene.uiCamera.width * 0.9f;
		}

		private static final int MARGIN = 2;
		private static final int BUTTON_HEIGHT = 16;
		private static final int FONT_SIZE = 9;
		private static final int BUTTON_HEIGHT_SM = 10;
		private static final int FONT_SIZE_SM = 6;

		private float maxHeight() {
			return PixelScene.uiCamera.height * 0.9f;
		}

		public WndBetterOptions( Image icon, String title, String message, String... options ) {
			super();

			int width = PixelScene.landscape() ? WIDTH_L : WIDTH_P;

			float pos = 0;
			if ( title != null ) {
				IconTitle tfTitle = new IconTitle( icon, title );
				tfTitle.setRect( 0, pos, width, 0 );
				add( tfTitle );

				pos = tfTitle.bottom() + 2 * MARGIN;
			}

			layoutBody( pos, message, options );
		}

		public WndBetterOptions( String title, String message, String... options ) {
			super();

			int width = PixelScene.landscape() ? WIDTH_L : WIDTH_P;

			float pos = MARGIN;
			if ( title != null ) {
				RenderedTextBlock tfTitle = PixelScene.renderTextBlock( title, 9 );
				tfTitle.hardlight( TITLE_COLOR );
				tfTitle.setPos( MARGIN, pos );
				tfTitle.maxWidth( width - MARGIN * 2 );
				add( tfTitle );

				pos = tfTitle.bottom() + 2 * MARGIN;
			}

			layoutBody( pos, message, options );
		}

		private void layoutBody( float pos, String message, String... options ) {
			int width = PixelScene.landscape() ? WIDTH_L : WIDTH_P;

			RenderedTextBlock tfMesage = PixelScene.renderTextBlock( 6 );
			tfMesage.text( message, width );
			tfMesage.setPos( 0, pos );
			add( tfMesage );

			pos = tfMesage.bottom() + 2 * MARGIN;

			ArrayList<RedButton> buttons = createButtons( options, BUTTON_HEIGHT, 9 );

			float newPos = tryLayout( buttons, pos, width, BUTTON_HEIGHT );

			if ( newPos < 0 ) {
				killAll( buttons );
				buttons = createButtons( options, BUTTON_HEIGHT_SM, FONT_SIZE_SM );
				newPos = tryLayout( buttons, pos, width, BUTTON_HEIGHT_SM );
			}
			pos = newPos;

			resize( width, (int) pos );
		}

		private float tryLayout( ArrayList<RedButton> buttons, float pos, int initialWidth, int btnHeight ) {
			int width = initialWidth;
			float newPos = pos;
			do {
				newPos = WndUseItem.layoutButtons( (ArrayList<RedButton>) buttons.clone(), width, pos, btnHeight );
				width += initialWidth / 2;
				width = Math.min( width, (int) maxWidth() );
			} while (newPos > maxHeight() && width < (int) maxWidth());
			if ( newPos < maxHeight() ) {
				return newPos;
			}
			return -1;
		}

		private ArrayList<RedButton> createButtons( String[] options, float btnHeight, int fontSize ) {
			ArrayList<RedButton> buttons = new ArrayList<>();
			for (int i = 0; i < options.length; i++) {
				final int index = i;
				RedButton btn = new RedButton( options[i], fontSize ) {
					@Override
					protected void onClick() {
						hide();
						onSelect( index );
					}
				};
				if ( hasIcon( i ) ) btn.icon( getIcon( i ) );
				btn.enable( enabled( i ) );
				add( btn );
				btn.setSize( btn.reqWidth(), btnHeight );
				buttons.add( btn );
			}
			return buttons;
		}

		private void killAll( ArrayList<RedButton> buttons ) {
			for (RedButton button : buttons) {
				button.killAndErase();
			}
		}

		protected boolean enabled( int index ) {
			return true;
		}

		protected void onSelect( int index ) {
		}

		protected boolean hasIcon( int index ) {
			return false;
		}

		protected Image getIcon( int index ) {
			return null;
		}
	}

}
