using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using PacketDotNet;
using Pidgin;
using static Pidgin.Parser;
using static Pidgin.Parser<char>;
using SharpPcap;
using SharpPcap.LibPcap;

namespace FTPparser
{
    public class FTPparser
    {
        private List<Message> messageBuffer;
        
        public FTPparser()
        {
            this.messageBuffer = new List<Message>();
        }

        /**
         * Returns message that is next in order from flow that has been processed.
         * 
         * @returns Message - next message of parsed flow, or null if no messages in messageBuffer
         */
        public Message getNextMessage()
        {
            if (messageBufferLen() == 0) return null;
            Message tmp = messageBuffer.First();
            messageBuffer.RemoveAt(0);
            return tmp;
        }

        /**
         * Returns count of elements remaining in messageBuffer that holds parsed messages from flow.
         * 
         * @returns int - length of messageBuffer list
         */
        public int messageBufferLen()
        {
            return messageBuffer.Count;
        }

        /**
         * Removes all elements from messageBuffer list.
         */
        public void resetMessageBuffer()
        {
            messageBuffer.Clear();
        }

        /**
         * Stores message object into messageBuffer
         *
         * @param Message - message to be stored
         */
        private void storeMessage(Message msg)
        {
            messageBuffer.Add(msg);
        }

        /**
         * parses request (MMMM [argument]) and returns Request object
         * Parse request command step by step using partial parsers. Partial parsers are ordered alphabetically (at least
         * by first character). Partial parsers are combined according to occurence of same characters on the same
         * position in command string. (PASV and PASS have same first three characters, so we need to determine syntactical
         * correctness using only the last character - the first three are parsed char by char for easier scaling if protocol
         * gets extended with new commands)
         *
         * @param string message
         * 
         * @returns Messsage - containing info from request message
         */
        private static Message parseRequest(string message)
        {
            // split input message by first occurence of space character -> separates command/code from argument
            var messageParts = message.Split(new[] {' '}, 2);
            
            // correct line ending CRLF
            Parser<char, char> crlf = Char('\r').Then(Char('\n'));

            // end parser to determine end of argument
            Parser<char, char> end = null;
            // cycles trough argument until crlf
            Parser<char, char> name = Any.Then(Rec((() => end)));

            //if crlf found end, else continue cycling
            end = crlf.Or(name);

            // checks for argument after command -> space argument CRLF
            Parser<char, char> argument = Char(' ').Then(name);
            
            Parser<char, string> appeCommand = String("PPE").Before(argument); //APPE
            Parser<char, string> alloCommand = String("LLO").Before(argument); //ALLO
            Parser<char, string> acctCommand = String("CCT").Before(argument); //ACCT     
            Parser<char, string> aborCommand = String("BOR").Before(crlf); //ABOR
            Parser<char, string> adatCommand = String("DAT").Before(argument); //ADAT
            Parser<char, string> authCommand = String("UTH").Before(argument); //AUTH
            Parser<char, string> avblCommand = String("VBL").Before(argument); //AVBL
            Parser<char, string> aCommands =
                Char('A').Then(OneOf(appeCommand, adatCommand, alloCommand, avblCommand, authCommand, acctCommand,
                    aborCommand));

            Parser<char, string> cwdCommand = String("WD").Before(argument); //CWD
            Parser<char, string> cdupCommand = String("DUP").Before(crlf); //CDUP
            Parser<char, string> cccCommand = String("CC").Before(crlf); //CCC
            Parser<char, string> confCommand = String("ONF").Before(crlf); //CONF
            Parser<char, string> csidCommand = String("SID").Before(crlf); //CSID
            Parser<char, string> clntommand = String("LNT").Before(argument); //CLNT
            Parser<char, string> cCommands =
                Char('C').Then(OneOf(cccCommand, cwdCommand, cdupCommand, confCommand, csidCommand, clntommand));

            Parser<char, string> deleCommand = String("ELE").Before(argument); //DELE
            Parser<char, string> dsizCommand = String("SIZ").Before(OneOf(argument, crlf)); //DSIZ
            Parser<char, string> dCommands = Char('D').Then(OneOf(deleCommand, dsizCommand));

            Parser<char, string> encCommand = String("NC").Before(argument); //ENC
            Parser<char, string> eprtCommand = String("RT").Before(argument); //EPRT
            Parser<char, string> epsvCommand = String("SV").Before(crlf); //EPSV
            Parser<char, string> epsvOrEprt = Char('P').Then(eprtCommand.Or(epsvCommand));
            Parser<char, string> eCommands = Char('E').Then(OneOf(encCommand, epsvOrEprt));

            Parser<char, string> featCommand = String("FEAT").Before(crlf); //FEAT

            Parser<char, string> helpCommand = String("ELP").Before(OneOf(argument, crlf)); //HELP
            Parser<char, string> hostCommand = String("OST").Before(argument); //HOST
            Parser<char, string> hCommands = Char('H').Then(OneOf(helpCommand, hostCommand));

            Parser<char, string> listCommand = String("IST").Before(OneOf(crlf, argument)); //LIST
            Parser<char, string> langCommand = String("ANG").Before(argument); //LANG
            Parser<char, string> lprtCommand = String("RT").Before(argument); //LPRT
            Parser<char, string> lpsvCommand = String("SV").Before(argument); //LPSV
            Parser<char, string> lprtOrLpsv = Char('P').Then(lprtCommand.Or(lpsvCommand));
            Parser<char, string> lCommands = Char('L').Then(OneOf(listCommand, langCommand, lprtOrLpsv));


            Parser<char, string> modeCommand = String("ODE").Before(argument); //MODE
            Parser<char, string> mkdCommand = String("KD").Before(argument); //MKD
            Parser<char, string> mdtmCommand = String("DTM").Before(argument); //MDTM
            Parser<char, string> mfctCommand = String("CT").Before(argument); //MFCT
            Parser<char, string> mffCommand = Char('F').Before(argument).ThenReturn("string"); //MFF
            Parser<char, string> mfmtCommand = String("MT").Before(argument); //MFMT
            Parser<char, string> mfCommands = Char('F').Then(OneOf(mfctCommand, mffCommand, mfmtCommand));
            Parser<char, string> micCommand = String("IC").Before(crlf); //MIC
            Parser<char, string> mlstCommand = String("ST").Before(argument); //MLST
            Parser<char, string> mlsdCommand = String("SD").Before(argument); //MLSD
            Parser<char, string> mlCommands = Char('L').Then(OneOf(mlstCommand, mlsdCommand));
            Parser<char, string> mCommands = Char('M').Then(OneOf(modeCommand, mkdCommand, mdtmCommand, mfCommands,
                micCommand, mlCommands));

            Parser<char, string> noopCommand = String("OOP").Before(crlf); //NOOP
            Parser<char, string> nlstCommand = String("LST").Before(argument); //NLST
            Parser<char, string> nCommands = Char('N').Then(OneOf(nlstCommand, noopCommand));

            Parser<char, string> optsCommand = String("OPTS").Before(argument); //OPTS

            /* returns string finally to avoid usage of <char, char> parser */
            Parser<char, string> passOrPassv = Char('A').Then(Char('S'))
                .Then(OneOf(Char('S').Before(argument), Char('V').Before(crlf))).ThenReturn("string"); //PASS PASV
            Parser<char, string> portCommand = String("ORT").Before(argument); //PORT
            Parser<char, string> pwdCommand = String("WD").Before(crlf); //PWD
            Parser<char, string> pbszCommand = String("BSZ").Before(argument); //PBSZ
            Parser<char, string> protCommand = String("ROT").Before(argument); //PROT
            Parser<char, string> pCommands =
                Char('P').Then(OneOf(portCommand, pwdCommand, passOrPassv, pbszCommand, protCommand));

            Parser<char, string> quitCommand = String("QUIT").Before(crlf); //QUIT

            Parser<char, string> retrCommand = String("TR").Before(argument);    //RETR
            Parser<char, string> restCommand = String("ST").Before(argument);    //REST
            Parser<char, string> reinCommand = String("IN").Before(crlf);    //REIN
            Parser<char, string> reCommands = Char('E').Then(OneOf(reinCommand, restCommand, retrCommand));
            Parser<char, string> rntoCommand = String("TO").Before(argument);    //RNTO
            Parser<char, string> rnfrCommand = String("FR");                    //RNFR
            Parser<char, string> rnCommands = Char('N').Then(OneOf(rntoCommand, rnfrCommand)).Before(argument);
            Parser<char, string> rmdCommand = String("MD").Before(argument);    //RMD
            Parser<char, string> rmdaCommand = String("MDA").Before(argument);    //RMDA
            Parser<char, string> rCommands = Char('R').Then(OneOf(rmdCommand, rnCommands, reCommands, rmdaCommand));


            Parser<char, string> siteCommand = String("TE").Before(argument);    //SITE
            Parser<char, string> sizeCommand = String("ZE").Before(argument);    //SIZE
            Parser<char, string> siCommands = Char('I').Then(siteCommand.Or(sizeCommand));
            Parser<char, string> storOrStou = Char('O')
                .Then(OneOf(Char('R').Before(argument), Char('U').Before(crlf))).ThenReturn("string");//STOR STOU
            Parser<char, string> struCommand = String("RU").Before(argument);    //STRU
            Parser<char, string> statCommand = String("AT").Before(argument);    //STAT
            Parser<char, string> stCommands = Char('T').Then(OneOf(struCommand, statCommand, storOrStou));
            Parser<char, string> smntCommand = String("MNT").Before(argument);    //SMNT
            Parser<char, string> systCommand = String("YST").Before(crlf);    //SYST
            Parser<char, string> spsvCommand = String("PSV").Before(crlf);    //SPSV
            Parser<char, string> sockCommand = String("OCK").Before(argument);
            Parser<char, string> sCommands =
                Char('S').Then(OneOf(siCommands, smntCommand, systCommand, sockCommand, stCommands, spsvCommand));

            Parser<char, string> typeCommand = String("YPE").Before(argument);    //TYPE
            Parser<char, string> thmbCommand = String("HMB").Before(argument);    //THMB
            Parser<char, string> tCommands = Char('T').Then(OneOf(typeCommand, thmbCommand));

            Parser<char, string> userCommand = String("USER").Before(argument);    //USER

            Parser<char, string> xcupCommand = String("CUP").Before(crlf);    //XCUP
            Parser<char, string> xmkdCommand = String("MKD").Before(argument); // XMKD
            Parser<char, string> xpwdCommand = String("PWD").Before(crlf);    //XPWD
            Parser<char, string> xrcpCommand = String("CP").Before(argument);  //XRCP
            Parser<char, string> xrmdCommand = String("MD").Before(argument);    //XRMD
            Parser<char, string> xrsqCommand = String("SQ").Before(argument);    //XRSQ
            Parser<char, string> xrCommands = Char('R').Then(OneOf(xrcpCommand, xrmdCommand, xrsqCommand));
            Parser<char, string> xsemCommand = String("M").Before(argument);    //XSEM
            Parser<char, string> xsenCommand = String("N").Before(argument);    //XSEN
            Parser<char, string> xseCommands = Char('S').Then(Char('E')).Then(OneOf(xsemCommand, xsenCommand));
            Parser<char, string> xCommands = Char('X').Then(OneOf(xrCommands, xseCommands, xcupCommand, xmkdCommand, xpwdCommand));

            // connect all partial parsers together
            Parser<char, string> command =
                OneOf(aCommands, cCommands, dCommands, eCommands, featCommand, hCommands, lCommands, mCommands,
                    nCommands, optsCommand, pCommands, quitCommand, rCommands, sCommands, tCommands, userCommand, xCommands);

            Message request = null;
            
            // try if message is syntactically correct
            try
            {
                command.ParseOrThrow(message);
            }
            catch (Exception e)
            {
                request = new Message("error");
                request.ErrorMessage = e.ToString();
                return request;
            }
            
            //if message with argument
            if (messageParts.Length > 1)
            {
                request = new Message(
                    "request"
                );
                request.Command = messageParts[0];
                request.CommandArgument = messageParts[1].Trim();
            }
            //if message without argument
            else
            {
                request = new Message("request");
                request.Command = messageParts[0].Trim();
            }
                
            
            return request;
        }
        
        /**
         * parses response (xyz message) and returns Response object
         * Parse response code step by step using partial parsers. Partial parsers are ordered from smallest ("ones")
         * to largest (hundreds). Each section is marked by commentary what response codes is parsed there.
         *
         * @param string message
         * 
         * @returns Mesage - containing info from response message
         */
        private static Message parseResponse(string message)
        {
            // split input message by first occurence of space character -> separates command/code from argument
            var messageParts = message.Split(new[] {' '}, 2);
            
            /* Parsing 1yz response codes */
            // 110
            Parser<char, char> r110 = Char('1').Then(Char('0'));
            // 120; 125
            Parser<char, char> r12z = Char('2').Then(Char('0').Or(Char('5')));
            // 150
            Parser<char, char> r150 = Char('5').Then(Char('0'));

            Parser<char, char> r1yz = OneOf(r110, r12z, r150);

            Parser<char, char> r100 = Char('1').Then(r1yz);

            /* Parsing 2yz response codes */
            // 200; 202
            Parser<char, char> r20z = Char('0').Then(Char('0').Or(Char('2')));
            // 211; 212; 213; 214; 215
            Parser<char, char> r21z = Char('1').Then(OneOf(Char('1'),
                Char('2'),
                Char('3'),
                Char('4'),
                Char('5')));
            // 220; 221; 225; 226; 227; 228; 229
            Parser<char, char> r22z = Char('2').Then(OneOf(Char('0'),
                                                                                Char('1'),
                                                                                Char('5'),
                                                                                Char('6'),
                                                                                Char('7'),
                                                                                Char('8'),
                                                                                Char('9')));
            // 230; 231; 232; 234
            Parser<char, char> r23z = Char('3').Then(OneOf(Char('0'),
                                                                    Char('1'),
                                                                    Char('2'),
                                                                    Char('4')));
            // 250; 257
            Parser<char, char> r25z = Char('5').Then(Char('0').Or(Char('7')));

            Parser<char, char> r2yz = OneOf(r20z, r21z, r22z, r23z, r25z);

            Parser<char, char> r200 = Char('2').Then(r2yz);

            /* Parsing 3yz codes */
            // 331; 332
            Parser<char, char> r33z = Char('3').Then(Char('1').Or(Char('2')));
            // 350
            Parser<char, char> r350 = Char('5').Then(Char('0'));

            Parser<char, char> r300 = Char('3').Then(OneOf(r33z, r350));

            /* Parsing 4yz codes */
            // 421; 425; 426
            Parser<char, char> r42z = Char('2').Then(OneOf(Char('1'), Char('5'), Char('6')));
            // 430; 434
            Parser<char, char> r43z = Char('3').Then(OneOf(Char('0'), Char('4')));
            // 450; 451; 452
            Parser<char, char> r45z = Char('5').Then(OneOf(Char('0'), Char('1'), Char('2')));

            Parser<char, char> r400 = Char('4').Then(OneOf(r42z, r43z, r45z));

            /* Parsing 5yz response codes */
            // 500; 501; 502; 503; 504
            Parser<char, char> r50z = Char('0').Then(OneOf(Char('0'),
                Char('1'),
                Char('2'),
                Char('3'),
                Char('4')));
            // 530; 532; 534
            Parser<char, char> r53z = Char('3').Then(OneOf(Char('0'),
                                                                                Char('2'),
                                                                                Char('4')));
            // 550; 551; 552; 553
            Parser<char, char> r55z = Char('5').Then(OneOf(Char('0'),
                Char('1'),
                Char('2'),
                Char('3')));
            
            Parser<char, char> r500 = Char('5').Then(OneOf(r50z, r53z, r55z));

            /* parsing 6yz response codes */
            // 631; 632; 633
            Parser<char, char> r63z = Char('3').Then(OneOf(Char('1'), 
                                                                                Char('2'), 
                                                                                Char('3')));
            
            Parser<char, char> r600 = Char('6').Then(OneOf(r63z));
            
            // all largest partial parsers being connected together
            Parser<char, char> responseCode = OneOf(r100, r200, r300, r400, r500, r600);

            Message response = null;
            
            /* try running response code parser
                if fails -> return error message
                if passes -> return response object*/
            try
            {
                responseCode.ParseOrThrow(messageParts[0]);
            }
            catch (Exception e)
            {
                response = new Message("error");
                response.ErrorMessage = e.ToString();
                return response;
            }
            
            response = new Message("response");

            response.ResponseCode = int.Parse(messageParts[0]);
            response.ResponseMessage = messageParts[1].Trim();
                

            return response;
            
        }
        
        /**
         * Parses one message of FTP protocol, chooses parser and return type according to
         * first character of the message:
         *     - int = response -> response code parser + returns Response object
         *     - char = request -> request command parser + returns Request object
         * returning different objects is not necessary, but gave sense to me because of attribute names
         * 
         * @param string message - message of FTP protocol to be parsed
         * @returns object -> Request/Response
         */
        public static Message parseMessage(string message)
        {
            //decides what type of message is parsed, response or request, how and why is described in function annotation
            if (!char.IsDigit(message[0]))
            {
                return parseRequest(message);
            }
            
            return parseResponse(message);
        }

        
        /**
         * Handler for packet arrival
         * Calling parser for every ethernet packet with src/dst port number 21 (FTP), then store
         * returned message object into messageBuffer for later withdrawal.
         * 
         */
        private void device_OnPacketArrival(object sender, CaptureEventArgs e)
        {
            // strip headers and get only FTP protocol message (skip first 66 bytes)
            byte[] appinfo = e.Packet.Data;
            appinfo = appinfo.Skip(54).ToArray();

            if (e.Packet.LinkLayerType == LinkLayers.Ethernet && appinfo.Length != 0)
            {
                var packet = Packet.ParsePacket(e.Packet.LinkLayerType, e.Packet.Data);
                var ethernetPacket = (EthernetPacket) packet;
                var tcp = packet.Extract<PacketDotNet.TcpPacket>();

                if (tcp != null)
                {
                    if (tcp.SourcePort == 21 || tcp.DestinationPort == 21)
                    { 
                        Message tmp = parseMessage(Encoding.ASCII.GetString(appinfo));
                        storeMessage(tmp);
                        return;
                    }
                }
            }

            return;

        }

        /**
         * Takes .pcap file, and assign handler function to process its content packet by packet,
         * each message that represents application layer content of packet is then stored in messageBuffer
         * list where it waits for withdrawal.
         *          
         * @param string filename - absolute path to .pcap file
         * 
         * @return bool - success/fail opening pcap stream
         */
        public bool parseFlow(string filename = "")
        {
            
            if (filename.Length == 0)
            {
                return false;
            }

            ICaptureDevice device;

            try
            {
                // Get an offline device
                device = new CaptureFileReaderDevice(filename);

                // Open the device
                device.Open();
            }
            catch (Exception e)
            {
                Console.WriteLine("Caught exception when opening file" + e.ToString());
                return false;
            }
            
            // Register our handler function to the 'packet arrival' event
            device.OnPacketArrival +=
                new PacketArrivalEventHandler(device_OnPacketArrival);
            
            // Start capture 'INFINTE' number of packets
            // This method will return when EOF reached.
            device.Capture();

            // Close the pcap device
            device.Close();

            return true;
        }
        
        /**
         * Entry point of parser application needed to run tests
         * no other usage (at least now) than to satisfy IDE because of missing entry point
         */
        public static void Main() {
            return;
        }
        
    }
    
}