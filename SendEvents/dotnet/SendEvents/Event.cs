using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Text;
using System.Threading.Tasks;

namespace SendEvents
{
    [DataContract]
    public class Event
    {
        [DataMember]
        public DateTime TimeStamp { get; set; }
        [DataMember]
        public int DeviceId { get; set; }
        [DataMember]
        public int Temperature { get; set; }
    }
}
