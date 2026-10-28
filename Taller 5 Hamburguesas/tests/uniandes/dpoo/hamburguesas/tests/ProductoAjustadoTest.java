package uniandes.dpoo.hamburguesas.tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uniandes.dpoo.hamburguesas.mundo.Ingrediente;
import uniandes.dpoo.hamburguesas.mundo.ProductoAjustado;
import uniandes.dpoo.hamburguesas.mundo.ProductoMenu;

public class ProductoAjustadoTest
{
    private ProductoAjustado ajustado1;
    private Ingrediente queso;
    private Ingrediente cebolla;

    @BeforeEach
    void setUp( ) throws Exception
    {
        ajustado1 = new ProductoAjustado( new ProductoMenu( "corral", 14000 ) );
        queso = new Ingrediente( "queso", 2500 );
        cebolla = new Ingrediente( "cebolla", 1000 );
    }

    @Test
    void testGetNombre( )
    {
        assertEquals( "corral", ajustado1.getNombre( ), "El nombre debe ser el del producto base." );
    }

    @Test
    void testGetPrecioSinAjustes( )
    {
        assertEquals( 14000, ajustado1.getPrecio( ), "Sin ajustes el precio debe ser el del producto base." );
    }

    // 14000 + 2500 del queso
    @Test
    void testGetPrecioConIngredienteAgregado( )
    {
        ajustado1.agregarIngrediente( queso );
        assertEquals( 16500, ajustado1.getPrecio( ), "Debe sumar el costo del ingrediente agregado." );
    }

    // El mismo ingrediente dos veces se cobra dos veces
    @Test
    void testGetPrecioMismoIngredienteDosVeces( )
    {
        ajustado1.agregarIngrediente( queso );
        ajustado1.agregarIngrediente( queso );
        assertEquals( 19000, ajustado1.getPrecio( ), "El ingrediente repetido debe cobrarse cada vez." );
    }

    // Quitar un ingrediente no rebaja el precio
    @Test
    void testGetPrecioConIngredienteEliminado( )
    {
        ajustado1.eliminarIngrediente( cebolla );
        assertEquals( 14000, ajustado1.getPrecio( ), "Quitar un ingrediente no debe cambiar el precio." );
    }

    @Test
    void testGenerarTextoFacturaSinAjustes( )
    {
        String esperado = "corral\n" + "            14000\n";
        assertEquals( esperado, ajustado1.generarTextoFactura( ), "El texto de la factura no es el esperado." );
    }

    @Test
    void testGenerarTextoFacturaConAjustes( )
    {
        ajustado1.agregarIngrediente( queso );
        ajustado1.eliminarIngrediente( cebolla );

        String esperado = "corral\n" + "    +queso                2500\n" + "    -cebolla\n" + "            16500\n";
        assertEquals( esperado, ajustado1.generarTextoFactura( ), "El texto de la factura no es el esperado." );
    }
}