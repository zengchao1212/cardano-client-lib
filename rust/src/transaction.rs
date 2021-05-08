use cardano_serialization_lib::fees::LinearFee;
use cardano_serialization_lib::utils::{to_bignum, Value, hash_transaction, make_vkey_witness};
use cardano_serialization_lib::tx_builder::TransactionBuilder;
use crate::address::{harden, get_root_key_from_mnemonic, get_private_key_from_mnemonic};
use cardano_serialization_lib::address::{StakeCredential, NetworkInfo, BaseAddress};
use cardano_serialization_lib::{TransactionInput, TransactionOutput, TransactionBody, Transaction, TransactionWitnessSet};
use cardano_serialization_lib::crypto::{TransactionHash, Vkeywitness, Vkeywitnesses, PrivateKey, Bip32PrivateKey};
use cbor_event::{self as cbor};

pub fn add_witness_and_sign(rawTxnInHex: &str, bech32PvtKey: &str) -> Vec<u8> {
    let bytesTxn = hex::decode(rawTxnInHex).unwrap();

    let prvKey = Bip32PrivateKey::from_bech32(bech32PvtKey).unwrap().to_raw_key();
    let mut transaction = Transaction::from_bytes(bytesTxn).unwrap();

    let txnBody = transaction.body();
    let txnBodyHash = hash_transaction(&txnBody);

    let vkey_witness = make_vkey_witness(&txnBodyHash, &prvKey);

    let mut txnWithnewssSet = TransactionWitnessSet::new();
    let mut vkey_witnesses = Vkeywitnesses::new();
    vkey_witnesses.add(&vkey_witness);

    txnWithnewssSet.set_vkeys(&vkey_witnesses);

    let wns = transaction.witness_set().vkeys();

    let finalTxn = Transaction::new(&txnBody, &txnWithnewssSet, None);

    cbor::cbor!(&finalTxn).unwrap()
}

#[cfg(test)]
mod tests {
    use crate::transaction::add_witness_and_sign;

    #[test]
    fn parse_and_sign_txn() {
        let str = "83a40081825820dcac27eed284adfa6ec02a6e8fa41f886faf267bff7a6e615df44ab8a311360d000182825839000916a5fed4589d910691b85addf608dceee4d9d60d4c9a4d2a925026c3229b212ba7ef8643cd8f7e38d6279336d61a40d228b036f40feed61a004c4b40825839008c5bf0f2af6f1ef08bb3f6ec702dd16e1c514b7e1d12f7549b47db9f4d943c7af0aaec774757d4745d1a2c8dd3220e6ec2c9df23f757a2f81a3af6f8c6021a00059d5d031a018fb29aa0f6";

        let mnemonic = "damp wish scrub sentence vibrant gauge tumble raven game extend winner acid side amused vote edge affair buzz hospital slogan patient drum day vital";

        let pvtKeyHash = "xprv10zlue93vusfclwsqafhyd48v56hfg4aqtptxwzd499q64upxlefaah3l9hw7wa3gy8p0j4a2caacpg7rd04twkypejpuvqrftqr0rh24rn8ay6kadm00t0h878l2fwhcpw6c87v2q746d4u7x6uxsnn84ugncknq";

        let transaction = add_witness_and_sign(&str, pvtKeyHash);
    }

}