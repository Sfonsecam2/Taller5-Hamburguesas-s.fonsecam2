package uniandes.dpoo.hamburguesas.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uniandes.dpoo.hamburguesas.mundo.Pedido;
import uniandes.dpoo.hamburguesas.mundo.ProductoMenu;

public class PedidoTest
{
    private Pedido pedido1;
    private ProductoMenu hamburguesa;
    private ProductoMenu papas;
    private File archivoFactura;

    @BeforeEach
    void setUp( ) throws Exception
    {
        pedido1 = new Pedido( "Samuel", "Calle 19 #1-1" );
        hamburguesa = new ProductoMenu( "hamburguesa", 10000 );
        papas = new ProductoMenu( "papas", 5000 );
        archivoFactura = new File( "./data/factura_prueba.txt" );
    }

    @AfterEach
    void tearDown( ) throws Exception
    {
        // Borro la factura que creó la prueba para no dejar basura
        if( archivoFactura.exists( ) )
        {
            archivoFactura.delete( );
        }
    }

    @Test
    void testGetNombreCliente( )
    {
        assertEquals( "Samuel", pedido1.getNombreCliente( ), "El nombre del cliente no es el esperado." );
    }

    // Cada pedido nuevo recibe el id siguiente
    @Test
    void testIdPedidosConsecutivos( )
    {
        Pedido pedido2 = new Pedido( "Ana", "Carrera 7 #40-62" );
        assertEquals( pedido1.getIdPedido( ) + 1, pedido2.getIdPedido( ), "Los id de pedidos seguidos deben ser consecutivos." );
    }

    @Test
    void testPrecioPedidoVacio( )
    {
        assertEquals( 0, pedido1.getPrecioTotalPedido( ), "Un pedido vacío debe costar 0." );
    }

    // 10000 + 5000 = 15000, IVA 19% = 2850, total 17850
    @Test
    void testGetPrecioTotalPedido( )
    {
        pedido1.agregarProducto( hamburguesa );
        pedido1.agregarProducto( papas );
        assertEquals( 17850, pedido1.getPrecioTotalPedido( ), "El total debe ser el neto más el IVA." );
    }

    @Test
    void testGenerarTextoFacturaPedidoVacio( )
    {
        String esperado = "Cliente: Samuel\n" + "Dirección: Calle 19 #1-1\n" + "----------------\n" + "----------------\n"
                + "Precio Neto:  0\n" + "IVA:          0\n" + "Precio Total: 0\n";
        assertEquals( esperado, pedido1.generarTextoFactura( ), "El texto de la factura no es el esperado." );
    }

    @Test
    void testGenerarTextoFacturaConProductos( )
    {
        pedido1.agregarProducto( hamburguesa );
        pedido1.agregarProducto( papas );

        String esperado = "Cliente: Samuel\n" + "Dirección: Calle 19 #1-1\n" + "----------------\n"
                + "hamburguesa\n" + "            10000\n" + "papas\n" + "            5000\n" + "----------------\n"
                + "Precio Neto:  15000\n" + "IVA:          2850\n" + "Precio Total: 17850\n";
        assertEquals( esperado, pedido1.generarTextoFactura( ), "El texto de la factura no es el esperado." );
    }

    // El archivo guardado debe tener exactamente el texto de la factura
    @Test
    void testGuardarFactura( ) throws Exception
    {
        pedido1.agregarProducto( hamburguesa );
        pedido1.guardarFactura( archivoFactura );

        assertTrue( archivoFactura.exists( ), "El archivo de la factura no se creó." );

        String textoArchivo = "";
        BufferedReader lector = new BufferedReader( new FileReader( archivoFactura ) );
        String linea = lector.readLine( );
        while( linea != null )
        {
            textoArchivo += linea + "\n";
            linea = lector.readLine( );
        }
        lector.close( );

        assertEquals( pedido1.generarTextoFactura( ), textoArchivo, "El archivo no tiene el texto de la factura." );
    }

    // Si la carpeta no existe debe lanzar FileNotFoundException
    @Test
    void testGuardarFacturaEnCarpetaQueNoExiste( )
    {
        File archivoMalo = new File( "./carpeta_que_no_existe/factura.txt" );
        assertThrows( FileNotFoundException.class, ( ) -> pedido1.guardarFactura( archivoMalo ), "Debió lanzarse FileNotFoundException." );
    }
}