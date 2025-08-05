using System;
using System.IO;
using System.Security.Cryptography;
using System.Text;

namespace LibUtil.Security
{
    public class AESCryptography
    {

        public static byte[] AESEncrypt(byte[] bytesToBeEncrypted, byte[] passwordBytes)
        {
            //return Savenet.Utility.AESCryptography.AESEncrypt(bytesToBeEncrypted, passwordBytes);

            byte[] encryptedBytes = null;

            // Set your salt here, change it to meet your flavor:
            // The salt bytes must be at least 8 bytes.
            byte[] saltBytes = new byte[] { 1, 2, 3, 4, 5, 6, 7, 8 };

            using (MemoryStream ms = new MemoryStream())
            {
                using (RijndaelManaged AES = new RijndaelManaged())
                {
                    //AES.KeySize = 256;
                    //AES.BlockSize = 256;

                    var key = new Rfc2898DeriveBytes(passwordBytes, saltBytes, 1000);
                    AES.Key = key.GetBytes(AES.KeySize / 8);
                    AES.IV = key.GetBytes(AES.BlockSize / 8);

                    //AES.Mode = CipherMode.CBC;
                    //AES.Padding = PaddingMode.PKCS7;
                    using (var cs = new CryptoStream(ms, AES.CreateEncryptor(), CryptoStreamMode.Write))
                    {
                        cs.Write(bytesToBeEncrypted, 0, bytesToBeEncrypted.Length);
                    }
                    encryptedBytes = ms.ToArray();
                }
            }

            return encryptedBytes;
        }

        public static byte[] AESDecrypt(byte[] bytesToBeDecrypted, byte[] passwordBytes)
        {
            //return Savenet.Utility.AESCryptography.AESDecrypt(bytesToBeDecrypted, passwordBytes);

            byte[] decryptedBytes = null;

            // Set your salt here, change it to meet your flavor:
            // The salt bytes must be at least 8 bytes.
            byte[] saltBytes = new byte[] { 1, 2, 3, 4, 5, 6, 7, 8 };

            using (MemoryStream ms = new MemoryStream())
            {
                using (RijndaelManaged AES = new RijndaelManaged())
                {
                    //AES.KeySize = 256;
                    //AES.BlockSize = 256;

                    var key = new Rfc2898DeriveBytes(passwordBytes, saltBytes, 1000);
                    AES.Key = key.GetBytes(AES.KeySize / 8);
                    AES.IV = key.GetBytes(AES.BlockSize / 8);

                    //AES.Mode = CipherMode.CBC;
                    //AES.Padding = PaddingMode.PKCS7;

                    using (var cs = new CryptoStream(ms, AES.CreateDecryptor(), CryptoStreamMode.Write))
                    {
                        cs.Write(bytesToBeDecrypted, 0, bytesToBeDecrypted.Length);
                        cs.FlushFinalBlock();
                    }
                    decryptedBytes = ms.ToArray();
                }
            }

            return decryptedBytes;
        }

        public static string AESDecryptText(string input, string password)
        {
            // Get the bytes of the string
            byte[] bytesToBeDecrypted = Convert.FromBase64String(input);
            byte[] passwordBytes = Encoding.UTF8.GetBytes(password);
            passwordBytes = SHA256.Create().ComputeHash(passwordBytes);

            byte[] bytesDecrypted = AESDecrypt(bytesToBeDecrypted, passwordBytes);

            string result = Encoding.UTF8.GetString(bytesDecrypted);

            return result;
        }

        public static string AESEncryptText(string input, string password)
        {
            // Get the bytes of the string
            byte[] bytesToBeEncrypted = Encoding.UTF8.GetBytes(input);
            byte[] passwordBytes = Encoding.UTF8.GetBytes(password);

            // Hash the password with SHA256
            passwordBytes = SHA256.Create().ComputeHash(passwordBytes);

            byte[] bytesEncrypted = AESEncrypt(bytesToBeEncrypted, passwordBytes);

            string result = Convert.ToBase64String(bytesEncrypted);

            return result;
        }
        public static void AESEncryptFile(string inputFile, string outputFile, string password)
        {

            byte[] bytesToBeEncrypted = File.ReadAllBytes(inputFile);
            byte[] passwordBytes = Encoding.UTF8.GetBytes(password);

            // Hash the password with SHA256
            passwordBytes = SHA256.Create().ComputeHash(passwordBytes);

            byte[] bytesEncrypted = AESEncrypt(bytesToBeEncrypted, passwordBytes);


            File.WriteAllBytes(outputFile, bytesEncrypted);
        }

        public static void AESDecryptFile(string inputFile, string outputFile, string password)
        {

            byte[] bytesToBeDecrypted = File.ReadAllBytes(inputFile);
            byte[] passwordBytes = Encoding.UTF8.GetBytes(password);
            passwordBytes = SHA256.Create().ComputeHash(passwordBytes);

            byte[] bytesDecrypted = AESDecrypt(bytesToBeDecrypted, passwordBytes);

            File.WriteAllBytes(outputFile, bytesDecrypted);
        }
        public static void AESEncryptTextToFile(string input, string outputFile, string password)
        {

            byte[] bytesToBeEncrypted = Encoding.UTF8.GetBytes(input);
            byte[] passwordBytes = Encoding.UTF8.GetBytes(password);

            // Hash the password with SHA256
            passwordBytes = SHA256.Create().ComputeHash(passwordBytes);

            byte[] bytesEncrypted = AESEncrypt(bytesToBeEncrypted, passwordBytes);


            File.WriteAllBytes(outputFile, bytesEncrypted);
        }

        public static void AESDecryptTextToFile(string input, string outputFile, string password)
        {

            byte[] bytesToBeDecrypted = Convert.FromBase64String(input);
            byte[] passwordBytes = Encoding.UTF8.GetBytes(password);
            passwordBytes = SHA256.Create().ComputeHash(passwordBytes);

            byte[] bytesDecrypted = AESDecrypt(bytesToBeDecrypted, passwordBytes);

            File.WriteAllBytes(outputFile, bytesDecrypted);
        }
        public static string AESEncryptFileToText(string inputFile, string password)
        {

            byte[] bytesToBeEncrypted = File.ReadAllBytes(inputFile);
            byte[] passwordBytes = Encoding.UTF8.GetBytes(password);

            // Hash the password with SHA256
            passwordBytes = SHA256.Create().ComputeHash(passwordBytes);

            byte[] bytesEncrypted = AESEncrypt(bytesToBeEncrypted, passwordBytes);

            string result = Convert.ToBase64String(bytesEncrypted);
            return result;

        }

        public static string AESDecryptFileToText(string inputFile, string password)
        {
            byte[] bytesToBeDecrypted = File.ReadAllBytes(inputFile);
            byte[] passwordBytes = Encoding.UTF8.GetBytes(password);
            passwordBytes = SHA256.Create().ComputeHash(passwordBytes);

            byte[] bytesDecrypted = AESDecrypt(bytesToBeDecrypted, passwordBytes);

            string result = Encoding.UTF8.GetString(bytesDecrypted);

            return result;
        }

    }
}
