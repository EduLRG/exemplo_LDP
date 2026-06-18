package com.mycompany.fivefieldkono.ui.control;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * Métodos utilitários relacionados com a rede.
 * <p>
 * Atualmente fornece a deteção do endereço IP local da máquina, ignorando
 * interfaces de loopback e endereços de VPN, para que o servidor mostre o
 * IP correto a partilhar com o adversário.
 *
 * @author Eduardo e Laurindo
 * @version 1.0
 */
public final class UtilRede {

    /** Construtor privado: classe apenas com métodos estáticos. */
    private UtilRede() { }

    /**
     * Descobre o endereço IP local da máquina na rede.
     *
     * @return o IP local (192.168.x, 10.x ou 172.x), ou "127.0.0.1" se não encontrar
     */
    public static String obterIpLocal() {
        try {
            Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces();
            while (ifaces.hasMoreElements()) {
                NetworkInterface iface = ifaces.nextElement();
                if (!iface.isUp() || iface.isLoopback()) continue;
                Enumeration<InetAddress> addrs = iface.getInetAddresses();
                while (addrs.hasMoreElements()) {
                    String ip = addrs.nextElement().getHostAddress();
                    if (ip.contains(".") &&
                        (ip.startsWith("192.168") || ip.startsWith("10.") || ip.startsWith("172."))) {
                        return ip;
                    }
                }
            }
        } catch (Exception ex) { /* ignora */ }
        return "127.0.0.1";
    }
}
