package com.bloxbean.cardano.client.account;

import com.bloxbean.cardano.client.common.Base32;
import com.bloxbean.cardano.client.common.Base58;
import com.bloxbean.cardano.client.common.Bech32;
import com.bloxbean.cardano.client.common.model.Network;
import com.bloxbean.cardano.client.common.model.Networks;
import com.bloxbean.cardano.client.exception.AddressExcepion;
import com.bloxbean.cardano.client.exception.CborSerializationException;
import com.bloxbean.cardano.client.transaction.spec.Transaction;
import com.bloxbean.cardano.client.util.HexUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.bouncycastle.util.encoders.Hex;

/**
 * Create and manage secrets, and perform account-based work such as signing transactions.
 */
public class Account {
    @JsonIgnore
    private String mnemonic;
    private String baseAddress;
    private String enterpriseAddress;
    private Network network;
    private int index;
    @JsonIgnore
    private String privateKey; //hex value
    @JsonIgnore
    private byte[] privateKeyBytes;
    @JsonIgnore
    private byte[] publicKeyBytes;

    /**
     * Create a new random mainnet account.
     */
    public Account() {
        this(Networks.mainnet(), 0);
    }

    /**
     * Create a new random mainnet account at index
     * @param index
     */
    public Account(int index) {
        this(Networks.mainnet(), index);
    }

    /**
     * Create a new random account for the network
     * @param network
     */
    public Account(Network network) {
        this(network, 0);
    }

    /**
     * Create a new random account for the network at index
     * @param network
     * @param index
     */
    public Account(Network network, int index) {
        this.network = network;
        this.index = index;
        generateNew();
    }

    public Account(Network network,String baseAddress,String enterpriseAddress) {
        this.network = network;
        this.baseAddress=baseAddress;
        this.enterpriseAddress=enterpriseAddress;
    }

    /**
     * Create a mainnet account from a mnemonic
     * @param mnemonic
     */
    public Account(String mnemonic) {
        this(Networks.mainnet(), mnemonic, 0);
    }

    /**
     * Create a mainnet account from a mnemonic at index
     * @param mnemonic
     */
    public Account(String mnemonic, int index) {
        this(Networks.mainnet(), mnemonic, index);
    }

    /**
     * Create a account for the network from a mnemonic
     * @param network
     * @param mnemonic
     */
    public Account(Network network, String mnemonic) {
        this(network, mnemonic, 0);
    }

    /**
     * Crate an account for the network from mnemonic at index
     * @param network
     * @param mnemonic
     * @param index
     */
    public Account(Network network, String mnemonic, int index) {
        this.network = network;
        this.mnemonic = mnemonic;
        this.index = index;
        getPrivateKey();
        baseAddress();
    }

    /**
     *
     * @return string a 24 word mnemonic
     */
    public String mnemonic() {
        return mnemonic;
    }

    /**
     *
     * @return baseAddress at index
     */
    public String baseAddress() {
//        if(this.baseAddress == null || this.baseAddress.trim().length() == 0) {
//            Network.ByReference refNetwork = new Network.ByReference();
//            refNetwork.network_id = network.network_id;
//            refNetwork.protocol_magic = network.protocol_magic;
//
//            this.baseAddress = CardanoJNAUtil.getBaseAddressByNetwork(mnemonic, index, refNetwork);
//        }
//
//        return this.baseAddress;
        return enterpriseAddress();
    }

    /**
     *
     * @return enterpriseAddress at index
     */
    public String enterpriseAddress() {
        return this.enterpriseAddress;
    }

    @JsonIgnore
    public String getBech32PrivateKey() {
        return privateKey;
    }

    @JsonIgnore
    public byte[] privateKeyBytes() {
        return privateKeyBytes;
    }

    @JsonIgnore
    public byte[] publicKeyBytes() {
        return publicKeyBytes;
    }

    /**
     * Sign a raw transaction with this account's private key
     * @param transaction
     * @return
     * @throws CborSerializationException
     */
    public String sign(Transaction transaction) throws CborSerializationException {
        throw new UnsupportedOperationException();
    }

    /**
     *
     * @param txnHex
     * @return
     * @throws CborSerializationException
     */
    public String sign(String txnHex) throws CborSerializationException {
        throw new UnsupportedOperationException();
    }

    public static byte[] toBytes(String address) throws AddressExcepion {
        if(address == null)
            return null;

        String hexStr;
        if(address.startsWith("addr")) { //Shelley address
            byte[] bytes= Bech32.decode(address).data;
            hexStr= Hex.toHexString(Base32.decode(bytes));
        } else { //Try for byron address
            hexStr = Hex.toHexString(Base58.decode(address));
        }

        if(hexStr.length() == 0)
            throw new AddressExcepion("Address to bytes failed");

        try {
            return HexUtil.decodeHexString(hexStr);
        } catch (Exception e) {
            throw new AddressExcepion("Address to bytes failed", e);
        }
    }

    public static String bytesToBase58Address(byte[] bytes) throws AddressExcepion { //byron address
        String address = Base58.encode(bytes);

        if(address.isEmpty())
            throw new AddressExcepion("Bytes cannot be converted to base58 address");

        return address;
    }

    public static String bytesToBech32(byte[] bytes) throws AddressExcepion {
        String bech32Address = Bech32.encode("addr",Base32.encode(bytes));
        if(bech32Address.isEmpty())
            throw new AddressExcepion("Bytes cannot be converted to bech32 address");

        return bech32Address;
    }

    private void generateNew() {
        throw new UnsupportedOperationException();
    }

    private void getPrivateKey() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        try {
            return baseAddress();
        } catch (Exception e) {
            return null;
        }
    }
}
