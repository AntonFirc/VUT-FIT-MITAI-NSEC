using System;
using System.IO;
using System.Text;
using FTPparser;
using Xunit;

namespace ParserTest
{
    public class Tests
    {
        private static string path = Directory.GetParent(Environment.CurrentDirectory).Parent.FullName;
        private static string pathBin = path + "/TestFiles/bin/";
        private static string pathFlow = path + "/TestFiles/flow/";

        private static string getBinaryContents(string filename)
        {
            byte[] fileBytes = File.ReadAllBytes(pathBin + filename);
            return Encoding.ASCII.GetString(fileBytes);
        }
        
        [Fact]
        public void TestUser()
        {
            Message tmp =  FTPparser.FTPparser.parseMessage(getBinaryContents("user.bin"));

            Assert.Equal("request", tmp.Type);
            Assert.Equal("USER", tmp.Command);
            Assert.Equal("csanders", tmp.CommandArgument);
        }

        [Fact]
        public void TestPassword()
        {
            Message tmp =  FTPparser.FTPparser.parseMessage(getBinaryContents("pass.bin"));

            Assert.Equal("request", tmp.Type);
            Assert.Equal("PASS", tmp.Command);
            Assert.Equal("ftp", tmp.CommandArgument);
        }

        [Fact]
        public void TestClnt()
        {
            FTPparser.FTPparser parser = new FTPparser.FTPparser();
            
            Message tmp =  FTPparser.FTPparser.parseMessage(getBinaryContents("clnt.bin"));

            Assert.Equal("request", tmp.Type);
            Assert.Equal("CLNT", tmp.Command);
            Assert.Equal("FlashFXP 3.4.0.1145", tmp.CommandArgument);
        }
        
        [Fact]
        public void TestCWD()
        {
            Message tmp =  FTPparser.FTPparser.parseMessage(getBinaryContents("cwd.bin"));

            Assert.Equal("request", tmp.Type);
            Assert.Equal("CWD", tmp.Command);
            Assert.Equal("uploads", tmp.CommandArgument);
        }

        [Fact]
        public void TestEPSV()
        {
            Message tmp =  FTPparser.FTPparser.parseMessage(getBinaryContents("epsv.bin"));

            Assert.Equal("request", tmp.Type);
            Assert.Equal("EPSV", tmp.Command);
        }

        [Fact]
        public void TestFeat()
        {
            Message tmp =  FTPparser.FTPparser.parseMessage(getBinaryContents("feat.bin"));

            Assert.Equal("request", tmp.Type);
            Assert.Equal("FEAT", tmp.Command);
        }

        [Fact]
        public void TestList()
        {
            Message tmp =  FTPparser.FTPparser.parseMessage(getBinaryContents("list.bin"));

            Assert.Equal("request", tmp.Type);
            Assert.Equal("LIST", tmp.Command);
        }

        [Fact]
        public void TestMdtm()
        {
            Message tmp =  FTPparser.FTPparser.parseMessage(getBinaryContents("mdtm.bin"));

            Assert.Equal("request", tmp.Type);
            Assert.Equal("MDTM", tmp.Command);
            Assert.Equal("resume.doc", tmp.CommandArgument);
        }

        [Fact]
        public void TestMkd()
        {
            Message tmp =  FTPparser.FTPparser.parseMessage(getBinaryContents("mkd.bin"));

            Assert.Equal("request", tmp.Type);
            Assert.Equal("MKD", tmp.Command);
            Assert.Equal("testdir", tmp.CommandArgument);
        }
        
        [Fact]
        public void TestNoop()
        {
            Message tmp =  FTPparser.FTPparser.parseMessage(getBinaryContents("noop.bin"));

            Assert.Equal("request", tmp.Type);
            Assert.Equal("NOOP", tmp.Command);
        }
        
        [Fact]
        public void TestOpts()
        {
            Message tmp =  FTPparser.FTPparser.parseMessage(getBinaryContents("opts.bin"));

            Assert.Equal("request", tmp.Type);
            Assert.Equal("OPTS", tmp.Command);
            Assert.Equal("utf8 on", tmp.CommandArgument);
        }

        [Fact]
        public void TestPasv()
        {
            Message tmp =  FTPparser.FTPparser.parseMessage(getBinaryContents("pasv.bin"));

            Assert.Equal("request", tmp.Type);
            Assert.Equal("PASV", tmp.Command);
        }
        
        [Fact]
        public void TestPwd()
        {
            Message tmp =  FTPparser.FTPparser.parseMessage(getBinaryContents("pwd.bin"));

            Assert.Equal("request", tmp.Type);
            Assert.Equal("PWD", tmp.Command);
        }

        [Fact]
        public void TestQuit()
        {
            Message tmp =  FTPparser.FTPparser.parseMessage(getBinaryContents("quit.bin"));

            Assert.Equal("request", tmp.Type);
            Assert.Equal("QUIT", tmp.Command);
        }

        [Fact]
        public void TestRetr()
        {
            Message tmp =  FTPparser.FTPparser.parseMessage(getBinaryContents("retr.bin"));

            Assert.Equal("request", tmp.Type);
            Assert.Equal("RETR", tmp.Command);
            Assert.Equal("resume.doc", tmp.CommandArgument);
        }

        [Fact]
        public void TestRmd()
        {
            Message tmp =  FTPparser.FTPparser.parseMessage(getBinaryContents("rmd.bin"));

            Assert.Equal("request", tmp.Type);
            Assert.Equal("RMD", tmp.Command);
            Assert.Equal("testerdir", tmp.CommandArgument);
        }

        [Fact]
        public void TestSite()
        {
            Message tmp =  FTPparser.FTPparser.parseMessage(getBinaryContents("site.bin"));

            Assert.Equal("request", tmp.Type);
            Assert.Equal("SITE", tmp.Command);
            Assert.Equal("CHMOD 777 resume.doc", tmp.CommandArgument);
        }

        [Fact]
        public void TestSize()
        {
            Message tmp =  FTPparser.FTPparser.parseMessage(getBinaryContents("size.bin"));

            Assert.Equal("request", tmp.Type);
            Assert.Equal("SIZE", tmp.Command);
            Assert.Equal("resume.doc", tmp.CommandArgument);
        }

        [Fact]
        public void TestStor()
        {
            Message tmp =  FTPparser.FTPparser.parseMessage(getBinaryContents("stor.bin"));

            Assert.Equal("request", tmp.Type);
            Assert.Equal("STOR", tmp.Command);
            Assert.Equal("README", tmp.CommandArgument);
        }

        [Fact]
        public void TestSyst()
        {
            Message tmp =  FTPparser.FTPparser.parseMessage(getBinaryContents("syst.bin"));

            Assert.Equal("request", tmp.Type);
            Assert.Equal("SYST", tmp.Command);
        }

        [Fact]
        public void TestType()
        {
            Message tmp =  FTPparser.FTPparser.parseMessage(getBinaryContents("type.bin"));

            Assert.Equal("request", tmp.Type);
            Assert.Equal("TYPE", tmp.Command);
            Assert.Equal("I", tmp.CommandArgument);
        }
        
        [Fact]
        public void TestResponse125()
        {
            Message tmp =  FTPparser.FTPparser.parseMessage(getBinaryContents("r125.bin"));

            Assert.Equal("response",tmp.Type);
            Assert.Equal(125, tmp.ResponseCode);
            Assert.Equal("Data connection already open; Transfer starting.", tmp.ResponseMessage);
        }

        [Fact]
        public void TestResponse150()
        {
            Message tmp =  FTPparser.FTPparser.parseMessage(getBinaryContents("r150.bin"));

            Assert.Equal("response",tmp.Type);
            Assert.Equal(150, tmp.ResponseCode);
            Assert.Equal("Opening ASCII mode data connection for file list", tmp.ResponseMessage);
        }

        [Fact]
        public void TestResponse200()
        {
            Message tmp =  FTPparser.FTPparser.parseMessage(getBinaryContents("r200.bin"));

            Assert.Equal("response",tmp.Type);
            Assert.Equal(200, tmp.ResponseCode);
            Assert.Equal("Type set to I", tmp.ResponseMessage);
        }
        
        [Fact]
        public void TestResponse213()
        {
            Message tmp =  FTPparser.FTPparser.parseMessage(getBinaryContents("r213.bin"));

            Assert.Equal("response",tmp.Type);
            Assert.Equal(213, tmp.ResponseCode);
            Assert.Equal("39424", tmp.ResponseMessage);
        }
        
        [Fact]
        public void TestResponse215()
        {
            Message tmp =  FTPparser.FTPparser.parseMessage(getBinaryContents("r215.bin"));

            Assert.Equal("response",tmp.Type);
            Assert.Equal(215, tmp.ResponseCode);
            Assert.Equal("UNIX Type: L8", tmp.ResponseMessage);
        }
        
        [Fact]
        public void TestResponse220()
        {
            Message tmp =  FTPparser.FTPparser.parseMessage(getBinaryContents("r220.bin"));
            
            Assert.Equal("response",tmp.Type);
            Assert.Equal(220, tmp.ResponseCode);
            Assert.Equal("ProFTPD 1.3.0a Server (ProFTPD Anonymous Server) [192.168.1.231]", tmp.ResponseMessage);
        }
        
        [Fact]
        public void TestResponse221()
        {
            Message tmp =  FTPparser.FTPparser.parseMessage(getBinaryContents("r221.bin"));

            Assert.Equal("response",tmp.Type);
            Assert.Equal(221, tmp.ResponseCode);
            Assert.Equal("Goodbye.", tmp.ResponseMessage);
        }
        
        [Fact]
        public void TestResponse226()
        {
            Message tmp =  FTPparser.FTPparser.parseMessage(getBinaryContents("r226.bin"));

            Assert.Equal("response",tmp.Type);
            Assert.Equal(226, tmp.ResponseCode);
            Assert.Equal("Transfer complete.", tmp.ResponseMessage);
        }
        
        [Fact]
        public void TestResponse227()
        {
            Message tmp =  FTPparser.FTPparser.parseMessage(getBinaryContents("r227.bin"));

            Assert.Equal("response",tmp.Type);
            Assert.Equal(227, tmp.ResponseCode);
            Assert.Equal("Entering Passive Mode (192,168,75,132,4,22).", tmp.ResponseMessage);
        }
        
        [Fact]
        public void TestResponse229()
        {
            Message tmp =  FTPparser.FTPparser.parseMessage(getBinaryContents("r229.bin"));

            Assert.Equal("response",tmp.Type);
            Assert.Equal(229, tmp.ResponseCode);
            Assert.Equal("Entering Extended Passive Mode (|||58612|)", tmp.ResponseMessage);
        }
        
        [Fact]
        public void TestResponse230()
        {
            Message tmp =  FTPparser.FTPparser.parseMessage(getBinaryContents("r230.bin"));

            Assert.Equal("response",tmp.Type);
            Assert.Equal(230, tmp.ResponseCode);
            Assert.Equal("Anonymous access granted, restrictions apply.", tmp.ResponseMessage);
        }
        
        [Fact]
        public void TestResponse250()
        {
            Message tmp =  FTPparser.FTPparser.parseMessage(getBinaryContents("r250.bin"));

            Assert.Equal("response",tmp.Type);
            Assert.Equal(250, tmp.ResponseCode);
            Assert.Equal("CWD command successful", tmp.ResponseMessage);
        }
        
        [Fact]
        public void TestResponse257()
        {
            Message tmp =  FTPparser.FTPparser.parseMessage(getBinaryContents("r257.bin"));

            Assert.Equal("response",tmp.Type);
            Assert.Equal(257, tmp.ResponseCode);
            Assert.Equal("\"/\" is current directory.", tmp.ResponseMessage);
        }
        
        [Fact]
        public void TestResponse331()
        {
            Message tmp =  FTPparser.FTPparser.parseMessage(getBinaryContents("r331.bin"));

            Assert.Equal("response",tmp.Type);
            Assert.Equal(331, tmp.ResponseCode);
            Assert.Equal("Anonymous login ok, send your complete email address as your password.", tmp.ResponseMessage);
        }
        
        [Fact]
        public void TestResponse502()
        {
            Message tmp =  FTPparser.FTPparser.parseMessage(getBinaryContents("r502.bin"));

            Assert.Equal("response",tmp.Type);
            Assert.Equal(502, tmp.ResponseCode);
            Assert.Equal("Unknown command 'utf8'.", tmp.ResponseMessage);
        }
        
        [Fact]
        public void TestResponse530()
        {
            Message tmp =  FTPparser.FTPparser.parseMessage(getBinaryContents("r530.bin"));

            Assert.Equal("response",tmp.Type);
            Assert.Equal(530, tmp.ResponseCode);
            Assert.Equal("Login incorrect.", tmp.ResponseMessage);
        }
        
        [Fact]
        public void TestResponse550()
        {
            Message tmp =  FTPparser.FTPparser.parseMessage(getBinaryContents("r550.bin"));

            Assert.Equal("response",tmp.Type);
            Assert.Equal(550, tmp.ResponseCode);
            Assert.Equal("testdir: File exists", tmp.ResponseMessage);
        }

       
        [Fact]
        public void TestUserPassSystFlow()
        {
            FTPparser.FTPparser parser = new FTPparser.FTPparser();
            
            parser.parseFlow(pathFlow + "user_pass_syst.pcap");
            
            Message tmp =  parser.getNextMessage();
            Assert.Equal("request",tmp.Type);
            Assert.Equal("USER", tmp.Command);
            Assert.Equal("csanders", tmp.CommandArgument);
            
            tmp =  parser.getNextMessage();
            Assert.Equal("response",tmp.Type);
            Assert.Equal(331, tmp.ResponseCode);
            Assert.Equal("Password required for csanders.", tmp.ResponseMessage);
            
            tmp =  parser.getNextMessage();
            Assert.Equal("request",tmp.Type);
            Assert.Equal("PASS", tmp.Command);
            Assert.Equal("echo", tmp.CommandArgument);
            
            tmp =  parser.getNextMessage();
            Assert.Equal("response",tmp.Type);
            Assert.Equal(230, tmp.ResponseCode);
            Assert.Equal("User csanders logged in.", tmp.ResponseMessage);
            
            tmp =  parser.getNextMessage();
            Assert.Equal("request",tmp.Type);
            Assert.Equal("SYST", tmp.Command);

            tmp =  parser.getNextMessage();
            Assert.Equal("response",tmp.Type);
            Assert.Equal(215, tmp.ResponseCode);
            Assert.Equal("UNIX Type: L8", tmp.ResponseMessage);
        }

        [Fact]
        public void TestMessageBuferCnt()
        {
            FTPparser.FTPparser parser = new FTPparser.FTPparser();
            
            parser.parseFlow(pathFlow + "user_pass_syst.pcap");
            
            Assert.Equal(6, parser.messageBufferLen());

            parser.getNextMessage();
            
            Assert.Equal(5, parser.messageBufferLen());

            parser.getNextMessage();
            parser.getNextMessage();
            
            Assert.Equal(3, parser.messageBufferLen());
            
            parser.resetMessageBuffer();
            
            Assert.Equal(0, parser.messageBufferLen());

            parser.getNextMessage();
        }
        
    }
}