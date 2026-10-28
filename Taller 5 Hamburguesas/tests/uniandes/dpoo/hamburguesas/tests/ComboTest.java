package uniandes.dpoo.hamburguesas.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uniandes.dpoo.hamburguesas.mundo.Combo;
import uniandes.dpoo.hamburguesas.mundo.ProductoMenu;

public class ComboTest
{
    private ArrayList<ProductoMenu> items;
    private Combo combo1;

    @BeforeEach
    void setUp( ) throws Exception
    {
        // Combo con hamburguesa de 10000 y papas de 5000, con 10% de descuento
        items = new ArrayList<ProductoMenu>( );
        items.add( new ProductoMenu( "hamburguesa", 10000 ) );
        items.add( new ProductoMenu( "papas", 5000 ) );
        combo1 = new Combo( "combo prueba", 0.1, items );
    }

    @Test
    void testGetNombre( )
    {
        assertEquals( "combo prueba", combo1.getNombre( ), "El nombre del combo no es el esperado." );
    }

    // 15000 menos el 10% = 13500
    @Test
    void testGetPrecioConDescuento( )
    {
        assertEquals( 13500, combo1.getPrecio( ), "El precio del combo no tiene bien aplicado el descuento." );
    }

    // Con descuento 0 debe costar lo mismo que los productos por separado
    @Test
    void testGetPrecioSinDescuento( )
    {
        Combo comboSinDescuento = new Combo( "combo sin descuento", 0, items );
        assertEquals( 15000, comboSinDescuento.getPrecio( ), "Un combo sin descuento debe costar la suma de sus productos." );
    }

    // Si cambio la lista original después de crear el combo, el combo no debe cambiar
    @Test
    void testCambiarListaOriginalNoAfectaCombo( )
    {
        items.add( new ProductoMenu( "gaseosa", 5000 ) );
        assertEquals( 13500, combo1.getPrecio( ), "El combo no debería cambiar si se modifica la lista con la que se creó." );
    }

    @Test
    void testGenerarTextoFactura( )
    {
        String esperado = "Combo combo prueba\n" + " Descuento: 0.1\n" + "            13500\n";
        assertEquals( esperado, combo1.generarTextoFactura( ), "El texto de la factura del combo no es el esperado." );
    }
}