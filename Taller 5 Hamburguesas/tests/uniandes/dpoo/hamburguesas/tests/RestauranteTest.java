package uniandes.dpoo.hamburguesas.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileNotFoundException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uniandes.dpoo.hamburguesas.excepciones.IngredienteRepetidoException;
import uniandes.dpoo.hamburguesas.excepciones.NoHayPedidoEnCursoException;
import uniandes.dpoo.hamburguesas.excepciones.ProductoFaltanteException;
import uniandes.dpoo.hamburguesas.excepciones.ProductoRepetidoException;
import uniandes.dpoo.hamburguesas.excepciones.YaHayUnPedidoEnCursoException;
import uniandes.dpoo.hamburguesas.mundo.Pedido;
import uniandes.dpoo.hamburguesas.mundo.ProductoMenu;
import uniandes.dpoo.hamburguesas.mundo.Restaurante;

public class RestauranteTest
{
    private Restaurante restaurante1;
    private File facturaCreada;

    @BeforeEach
    void setUp( ) throws Exception
    {
        restaurante1 = new Restaurante( );
        // El restaurante guarda las facturas en ./facturas/, así que la carpeta tiene que existir
        new File( "./facturas/" ).mkdirs( );
        facturaCreada = null;
    }

    @AfterEach
    void tearDown( ) throws Exception
    {
        if( facturaCreada != null && facturaCreada.exists( ) )
        {
            facturaCreada.delete( );
        }
    }

    private File archivo( String nombre )
    {
        return new File( "./data/pruebas/" + nombre );
    }

    @Test
    void testRestauranteVacio( )
    {
        assertTrue( restaurante1.getIngredientes( ).isEmpty( ), "No debería haber ingredientes." );
        assertTrue( restaurante1.getMenuBase( ).isEmpty( ), "No debería haber productos." );
        assertTrue( restaurante1.getMenuCombos( ).isEmpty( ), "No debería haber combos." );
        assertTrue( restaurante1.getPedidos( ).isEmpty( ), "No debería haber pedidos." );
        assertNull( restaurante1.getPedidoEnCurso( ), "No debería haber pedido en curso." );
    }

    // Archivos correctos, con líneas vacías en medio
    @Test
    void testCargarInformacionRestaurante( ) throws Exception
    {
        restaurante1.cargarInformacionRestaurante( archivo( "ingredientes_ok.txt" ), archivo( "menu_ok.txt" ), archivo( "combos_ok.txt" ) );

        assertEquals( 3, restaurante1.getIngredientes( ).size( ), "No se cargaron bien los ingredientes." );
        assertEquals( 3, restaurante1.getMenuBase( ).size( ), "No se cargó bien el menú." );
        assertEquals( 2, restaurante1.getMenuCombos( ).size( ), "No se cargaron bien los combos." );

        assertEquals( "queso", restaurante1.getIngredientes( ).get( 2 ).getNombre( ) );
        assertEquals( 2500, restaurante1.getIngredientes( ).get( 2 ).getCostoAdicional( ) );
        assertEquals( "papas", restaurante1.getMenuBase( ).get( 1 ).getNombre( ) );
        assertEquals( 5000, restaurante1.getMenuBase( ).get( 1 ).getPrecio( ) );

        // combo completo: 10000 + 5000 + 4000 = 19000, con 20% de descuento = 15200
        assertEquals( "combo completo", restaurante1.getMenuCombos( ).get( 1 ).getNombre( ) );
        assertEquals( 15200, restaurante1.getMenuCombos( ).get( 1 ).getPrecio( ), "El precio del combo cargado no es el esperado." );
    }

    // Los archivos reales de la carpeta data
    @Test
    void testCargarArchivosDelRestaurante( ) throws Exception
    {
        restaurante1.cargarInformacionRestaurante( new File( "./data/ingredientes.txt" ), new File( "./data/menu.txt" ), new File( "./data/combos.txt" ) );

        assertEquals( 15, restaurante1.getIngredientes( ).size( ) );
        assertEquals( 22, restaurante1.getMenuBase( ).size( ) );
        assertEquals( 4, restaurante1.getMenuCombos( ).size( ) );
    }

    @Test
    void testIngredienteRepetido( )
    {
        assertThrows( IngredienteRepetidoException.class,
                ( ) -> restaurante1.cargarInformacionRestaurante( archivo( "ingredientes_repetido.txt" ), archivo( "menu_ok.txt" ), archivo( "combos_ok.txt" ) ) );
    }

    @Test
    void testProductoRepetido( )
    {
        assertThrows( ProductoRepetidoException.class,
                ( ) -> restaurante1.cargarInformacionRestaurante( archivo( "ingredientes_ok.txt" ), archivo( "menu_repetido.txt" ), archivo( "combos_ok.txt" ) ) );
    }

    @Test
    void testComboRepetido( )
    {
        assertThrows( ProductoRepetidoException.class,
                ( ) -> restaurante1.cargarInformacionRestaurante( archivo( "ingredientes_ok.txt" ), archivo( "menu_ok.txt" ), archivo( "combos_repetido.txt" ) ) );
    }

    @Test
    void testProductoFaltanteEnCombo( )
    {
        assertThrows( ProductoFaltanteException.class,
                ( ) -> restaurante1.cargarInformacionRestaurante( archivo( "ingredientes_ok.txt" ), archivo( "menu_ok.txt" ), archivo( "combos_producto_faltante.txt" ) ) );
    }

    @Test
    void testPrecioInvalido( )
    {
        assertThrows( NumberFormatException.class,
                ( ) -> restaurante1.cargarInformacionRestaurante( archivo( "ingredientes_precio_invalido.txt" ), archivo( "menu_ok.txt" ), archivo( "combos_ok.txt" ) ) );
    }

    @Test
    void testArchivoQueNoExiste( )
    {
        assertThrows( FileNotFoundException.class,
                ( ) -> restaurante1.cargarInformacionRestaurante( archivo( "no_existe.txt" ), archivo( "menu_ok.txt" ), archivo( "combos_ok.txt" ) ) );
    }

    @Test
    void testIniciarPedido( ) throws Exception
    {
        restaurante1.iniciarPedido( "Samuel", "Calle 19 #1-1" );

        assertNotNull( restaurante1.getPedidoEnCurso( ), "Debería haber un pedido en curso." );
        assertEquals( "Samuel", restaurante1.getPedidoEnCurso( ).getNombreCliente( ) );
    }

    @Test
    void testIniciarPedidoConPedidoEnCurso( ) throws Exception
    {
        restaurante1.iniciarPedido( "Samuel", "Calle 19 #1-1" );

        assertThrows( YaHayUnPedidoEnCursoException.class, ( ) -> restaurante1.iniciarPedido( "Ana", "Carrera 7 #40-62" ) );
        assertEquals( "Samuel", restaurante1.getPedidoEnCurso( ).getNombreCliente( ), "El pedido en curso no debió cambiar." );
    }

    @Test
    void testCerrarPedidoSinPedidoEnCurso( )
    {
        assertThrows( NoHayPedidoEnCursoException.class, ( ) -> restaurante1.cerrarYGuardarPedido( ) );
    }

    // Al cerrar: se guarda la factura, el pedido queda en la lista y ya no hay pedido en curso
    @Test
    void testCerrarYGuardarPedido( ) throws Exception
    {
        restaurante1.iniciarPedido( "Samuel", "Calle 19 #1-1" );
        Pedido pedido = restaurante1.getPedidoEnCurso( );
        pedido.agregarProducto( new ProductoMenu( "hamburguesa", 10000 ) );

        restaurante1.cerrarYGuardarPedido( );

        facturaCreada = new File( "./facturas/factura_" + pedido.getIdPedido( ) + ".txt" );
        assertTrue( facturaCreada.exists( ), "No se creó el archivo de la factura." );
        assertNull( restaurante1.getPedidoEnCurso( ), "Después de cerrar no debería haber pedido en curso." );
        assertEquals( 1, restaurante1.getPedidos( ).size( ), "El pedido cerrado debe quedar en la lista de pedidos." );
        assertSame( pedido, restaurante1.getPedidos( ).get( 0 ) );
    }
}