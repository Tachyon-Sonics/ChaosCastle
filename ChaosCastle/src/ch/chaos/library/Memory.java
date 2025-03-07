package ch.chaos.library;

import ch.pitchtech.modula.runtime.Runtime;
import ch.pitchtech.modula.runtime.Runtime.IRef;
import ch.pitchtech.modula.runtime.StorageImpl;

public class Memory {

    private static Memory instance;


    private Memory() {
        instance = this; // Set early to handle circular dependencies
    }

    public static Memory instance() {
        if (instance == null)
            new Memory(); // will set 'instance'
        return instance;
    }

    // CONST


    public static final int tagDone = 0;
    public static final int tagIgnore = 1;
    public static final int tagMore = 2;
    public static final int tagSkip = 3;
    public static final short tagUser = Short.MIN_VALUE /* MIN(INTEGER) */;
    public static final int NO = 0 /* ORD(FALSE) */;
    public static final int YES = 1 /* ORD(TRUE) */;

    // TYPE

    public static final Runtime.Range SET16_r = new Runtime.Range(0, 15);


    public static class TagItem { // RECORD

        public int tag;
        public short x;
        // CASE "x" {
        public long data; // 0
        public int lint; // 1
        public Object addr; // 2
        public Runtime.RangeSet hset = new Runtime.RangeSet(SET16_r); // 3
        public Runtime.RangeSet bset = new Runtime.RangeSet(SET16_r); // 3
        // }


        public int getTag() {
            return this.tag;
        }

        public void setTag(int tag) {
            this.tag = tag;
        }

        public short getX() {
            return this.x;
        }

        public void setX(short x) {
            this.x = x;
        }

        public long getData() {
            return this.data;
        }

        public void setData(long data) {
            this.data = data;
        }

        public int getLint() {
            return this.lint;
        }

        public void setLint(int lint) {
            this.lint = lint;
        }

        public Object getAddr() {
            return this.addr;
        }

        public void setAddr(Object addr) {
            this.addr = addr;
        }

        public Runtime.RangeSet getHset() {
            return this.hset;
        }

        public void setHset(Runtime.RangeSet hset) {
            this.hset = hset;
        }

        public Runtime.RangeSet getBset() {
            return this.bset;
        }

        public void setBset(Runtime.RangeSet bset) {
            this.bset = bset;
        }

        public void copyFrom(TagItem other) {
            this.tag = other.tag;
            this.x = other.x;
            this.data = other.data;
            this.lint = other.lint;
            this.addr = other.addr;
            this.hset.copyFrom(other.hset);
            this.bset.copyFrom(other.bset);
        }

        public TagItem newCopy() {
            TagItem copy = new TagItem();
            copy.copyFrom(this);
            return copy;
        }

    }

    public static class Node { // RECORD

        public Node next /* POINTER */;
        public Node prev /* POINTER */;
        public Object data;


        public Node() {
            this(null);
        }

        public Node(Object data) {
            this.data = data;
        }

        public Node getNext() {
            return this.next;
        }

        public void setNext(Node next) {
            this.next = next;
        }

        public Node getPrev() {
            return this.prev;
        }

        public void setPrev(Node prev) {
            this.prev = prev;
        }

        public Object getData() {
            return this.data;
        }

        public void copyFrom(Node other) {
            this.next = other.next;
            this.prev = other.prev;
        }

        public Node newCopy() {
            Node copy = new Node(this.data);
            copy.copyFrom(this);
            return copy;
        }

    }

    public static class List { // RECORD

        public Node head = new Node(null);
        public Node tail = new Node(null);


        public Node getHead() {
            return this.head;
        }

        public void setHead(Node head) {
            this.head = head;
        }

        public Node getTail() {
            return this.tail;
        }

        public void setTail(Node tail) {
            this.tail = tail;
        }

        public void copyFrom(List other) {
            this.head.copyFrom(other.head);
            this.tail.copyFrom(other.tail);
        }

        public List newCopy() {
            List copy = new List();
            copy.copyFrom(this);
            return copy;
        }

    }

    @FunctionalInterface
    public static interface MemHandler { // PROCEDURE Type

        public boolean invoke();
    }

    // VAR


    public boolean multiThread;
    public boolean isMsb;


    public boolean isMultiThread() {
        return this.multiThread;
    }

    public void setMultiThread(boolean multiThread) {
        this.multiThread = multiThread;
    }

    public boolean isIsMsb() {
        return this.isMsb;
    }

    public void setIsMsb(boolean isMsb) {
        this.isMsb = isMsb;
    }

    // IMPL


    class TagItemImpl extends TagItem {

        private TagItemImpl next;


        public TagItemImpl append(int tag, Object value) {
            TagItemImpl tagItem = createTagItem(tag, value);
            TagItemImpl previous = this;
            while (previous.next != null)
                previous = previous.next;
            previous.next = tagItem;
            return this;
        }

        @Override
        public String toString() {
            StringBuilder result = new StringBuilder();
            toString(result);
            return result.toString();
        }

        private void toString(StringBuilder result) {
            if (tag == tagMore) {
                TagItemImpl moreTags = (TagItemImpl) addr;
                moreTags.toString(result);
                return;
            }

            if (result.length() > 0)
                result.append(", ");
            if (tag >= 0) {
                result.append("S" + tag);
            } else {
                result.append("U" + (tag - Short.MIN_VALUE));
            }
            result.append("=");
            if (addr != null) {
                if (addr instanceof Number number) {
                    result.append(number.toString());
                } else {
                    result.append(addr.getClass().getSimpleName());
                }
            } else if (data != 0) {
                result.append(data);
            } else {
                result.append(lint);
            }
            if (next != null) {
                next.toString(result);
            }
        }

    }


    private TagItemImpl createTagItem(int tag, Object value) {
        TagItemImpl tagItem = new TagItemImpl();
        tagItem.setTag(tag);
        tagItem.setAddr(value);
        if (value instanceof Number number) {
            tagItem.setData(number.longValue());
            tagItem.setLint(number.intValue());
        }
        return tagItem;
    }

    public Object AllocMem(long size) {
        return StorageImpl.allocate((int) size);
    }

    public Object AllocShared(long size) {
        return AllocMem(size);
    }

    public boolean LockMem(Object base, long offset, long length, boolean modify, boolean wait) {
        // todo implement LockMem
        throw new UnsupportedOperationException("Not implemented: LockMem");
    }

    public void UnlockMem(Object base, long offset, long length) {
        // todo implement UnlockMem
        throw new UnsupportedOperationException("Not implemented: UnlockMem");
    }

    public boolean TryLock(Object base, boolean modify) {
        // todo implement TryLock
        throw new UnsupportedOperationException("Not implemented: TryLock");
    }

    public void LockR(Object base) {
        // todo implement LockR
        throw new UnsupportedOperationException("Not implemented: LockR");
    }

    public void LockW(Object base) {
        // todo implement LockW
        throw new UnsupportedOperationException("Not implemented: LockW");
    }

    public void Unlock(Object base) {
        // todo implement Unlock
        throw new UnsupportedOperationException("Not implemented: Unlock");
    }

    public void AddMemHandler(MemHandler Handler) {
        // todo implement AddMemHandler
        throw new UnsupportedOperationException("Not implemented: AddMemHandler");
    }

    public void RemMemHandler(MemHandler Handler) {
        // todo implement RemMemHandler
        throw new UnsupportedOperationException("Not implemented: RemMemHandler");
    }

    public void FreeMem(/* VAR */ Runtime.IRef<Object> ptr) {
        ptr.set(null);
    }

    public Object TAG1(int t1, Object v1) {
        return createTagItem(t1, v1);
    }

    public Object TAG2(int t1, Object v1, int t2, Object v2) {
        return createTagItem(t1, v1).append(t2, v2);
    }

    public Object TAG3(int t1, Object v1, int t2, Object v2, int t3, Object v3) {
        return createTagItem(t1, v1).append(t2, v2).append(t3, v3);
    }

    public Object TAG4(int t1, Object v1, int t2, Object v2, int t3, Object v3, int t4, Object v4) {
        return createTagItem(t1, v1).append(t2, v2).append(t3, v3).append(t4, v4);
    }

    public Object TAG5(int t1, Object v1, int t2, Object v2, int t3, Object v3, int t4, Object v4, int t5, Object v5) {
        return createTagItem(t1, v1).append(t2, v2).append(t3, v3).append(t4, v4).append(t5, v5);
    }

    public Object TAG6(int t1, Object v1, int t2, Object v2, int t3, Object v3, int t4, Object v4, int t5, Object v5, int t6, Object v6) {
        return createTagItem(t1, v1).append(t2, v2).append(t3, v3).append(t4, v4).append(t5, v5).append(t6, v6);
    }

    public Object TAG7(int t1, Object v1, int t2, Object v2, int t3, Object v3, int t4, Object v4, int t5, Object v5, int t6, Object v6, int t7, Object v7) {
        return createTagItem(t1, v1).append(t2, v2).append(t3, v3).append(t4, v4).append(t5, v5).append(t6, v6).append(t7, v7);
    }

    public Object TAG8(int t1, Object v1, int t2, Object v2, int t3, Object v3, int t4, Object v4, int t5, Object v5, int t6, Object v6, int t7, Object v7,
            int t8, Object v8) {
        return createTagItem(t1, v1).append(t2, v2).append(t3, v3).append(t4, v4).append(t5, v5).append(t6, v6).append(t7, v7).append(t8, v8);
    }

    public TagItem NextTag(/* VAR */ Runtime.IRef<TagItem> tags) {
        TagItemImpl tagItem = (TagItemImpl) tags.get();
        TagItemImpl nextTagItem = tagItem.next;
        tags.set(nextTagItem);
        return nextTagItem;
    }

    public int StrLength(Runtime.IRef<String> str) {
        return str.get().length();
    }

    public void CopyStr(Runtime.IRef<String> src, Runtime.IRef<String> dst, int maxLength) {
        if (src.get().length() > maxLength)
            dst.set(src.get().substring(0, maxLength));
        else
            dst.set(src.get());
    }

    public Object ADS(String str) {
        return new Runtime.Ref<>(str);
    }

    public void InitList(/* VAR */ List list) {
        list.head.prev = null;
        list.head.next = list.tail;
        list.tail.prev = list.head;
        list.tail.next = null;
    }

    /* PATCH BEGIN */

    public Object First(/* VAR */ List list) {
        return list.head.next.data;
    }

    public Node FirstNode(List list) {
        return list.head.next;
    }

    public Object Last(/* VAR */ List list) {
        return list.tail.prev.data;
    }

    public Object Head(/* VAR */ List list) {
        return list.head.data;
    }

    public Object Tail(/* VAR */ List list) {
        return list.tail.data;
    }

    public Node TailNode(List list) {
        return list.tail;
    }

    public boolean Empty(/* VAR */ List list) {
        return list.tail == list.head.next;
    }

    public Object Prev(/* VAR */ Node node) {
        return node.prev.data;
    }

    public Node PrevNode(Node node) {
        return node.prev;
    }

    public Object Next(/* VAR */ Node node) {
        return node.next.data;
    }

    public Node NextNode(Node node) {
        return node.next;
    }

    /* PATCH END */

    public void AddHead(/* VAR */ List list, /* VAR */ Node node) {
        node.prev = list.head;
        node.next = list.head.next;
        list.head.next.prev = node;
        list.head.next = node;
    }

    public void AddTail(/* VAR */ List list, /* VAR */ Node node) {
        node.next = list.tail;
        node.prev = list.tail.prev;
        list.tail.prev.next = node;
        list.tail.prev = node;
    }

    public void AddBefore(/* VAR */ Node before, /* VAR */ Node node) {
        node.prev = before.prev;
        node.next = before;
        before.prev.next = node;
        before.prev = node;
    }

    public void Remove(/* VAR */ Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
        node.prev = null;
        node.next = null;
    }

    public byte sReadInt8(Object buffer, long offset) {
        // todo implement sReadInt8
        throw new UnsupportedOperationException("Not implemented: sReadInt8");
    }

    public void sWriteInt8(Object buffer, long offset, byte value) {
        // todo implement sWriteInt8
        throw new UnsupportedOperationException("Not implemented: sWriteInt8");
    }

    public short sReadMInt16(Object buffer, long offset) {
        // todo implement sReadMInt16
        throw new UnsupportedOperationException("Not implemented: sReadMInt16");
    }

    public void sWriteMInt16(Object buffer, long offset, short value) {
        // todo implement sWriteMInt16
        throw new UnsupportedOperationException("Not implemented: sWriteMInt16");
    }

    public short sReadLInt16(Object buffer, long offset) {
        // todo implement sReadLInt16
        throw new UnsupportedOperationException("Not implemented: sReadLInt16");
    }

    public void sWriteLInt16(Object buffer, long offset, short value) {
        // todo implement sWriteLInt16
        throw new UnsupportedOperationException("Not implemented: sWriteLInt16");
    }

    public int sReadMInt32(Object buffer, long offset) {
        // todo implement sReadMInt32
        throw new UnsupportedOperationException("Not implemented: sReadMInt32");
    }

    public void sWriteMInt32(Object buffer, long offset, int value) {
        // todo implement sWriteMInt32
        throw new UnsupportedOperationException("Not implemented: sWriteMInt32");
    }

    public int sReadLInt32(Object buffer, long offset) {
        // todo implement sReadLInt32
        throw new UnsupportedOperationException("Not implemented: sReadLInt32");
    }

    public void sWriteLInt32(Object buffer, long offset, int value) {
        // todo implement sWriteLInt32
        throw new UnsupportedOperationException("Not implemented: sWriteLInt32");
    }

    public float sReadMReal32(Object buffer, long offset) {
        // todo implement sReadMReal32
        throw new UnsupportedOperationException("Not implemented: sReadMReal32");
    }

    public void sWriteMReal32(Object buffer, long offset, float value) {
        // todo implement sWriteMReal32
        throw new UnsupportedOperationException("Not implemented: sWriteMReal32");
    }

    public float sReadLReal32(Object buffer, long offset) {
        // todo implement sReadLReal32
        throw new UnsupportedOperationException("Not implemented: sReadLReal32");
    }

    public void sWriteLReal32(Object buffer, long offset, float value) {
        // todo implement sWriteLReal32
        throw new UnsupportedOperationException("Not implemented: sWriteLReal32");
    }

    public double sReadMReal64(Object buffer, long offset) {
        // todo implement sReadMReal64
        throw new UnsupportedOperationException("Not implemented: sReadMReal64");
    }

    public void sWriteMReal64(Object buffer, long offset, double value) {
        // todo implement sWriteMReal64
        throw new UnsupportedOperationException("Not implemented: sWriteMReal64");
    }

    public double sReadLReal64(Object buffer, long offset) {
        // todo implement sReadLReal64
        throw new UnsupportedOperationException("Not implemented: sReadLReal64");
    }

    public void sWriteLReal64(Object buffer, long offset, double value) {
        // todo implement sWriteLReal64
        throw new UnsupportedOperationException("Not implemented: sWriteLReal64");
    }

    public short GetBitField(Object src, long offset, short size) {
        if (src instanceof IRef<?> ref) {
            if (ref.getDataType().equals(Short.class)) {
                int mask = (1 << size) - 1;
                @SuppressWarnings("unchecked")
                IRef<Short> sRef = (IRef<Short>) ref;
                int value = sRef.get();
                value = (value >>> offset) & mask;
                return (short) value;
            }
        }
        // todo implement GetBitField
        throw new UnsupportedOperationException("Not implemented: GetBitField");
    }

    public void SetBitField(Object dst, long offset, short size, short data) {
        if (dst instanceof IRef<?> ref) {
            if (ref.getDataType().equals(Short.class)) {
                @SuppressWarnings("unchecked")
                IRef<Short> sRef = (IRef<Short>) ref;
                int mask = (1 << size) - 1;
                int value = (data & mask) << offset;

                mask = (mask << offset);
                int clearMask = (-1 ^ mask);
                int previous = sRef.get();
                int updated = (previous & clearMask) | (value & mask);
                assert updated >= 0;
                sRef.set((short) (updated & 0xff));
                return;
            }
        }
        throw new UnsupportedOperationException("Not implemented: SetBitField");
    }

    private static TagItemImpl next(TagItemImpl tag) {
        if (tag.tag == tagMore) {
            if (tag.next != null)
                throw new IllegalStateException(); // Hopefully never used
            return (TagItemImpl) tag.addr;
        } else {
            return tag.next;
        }
    }

    static long tagData(TagItem tags0, int tag, long defaultValue) {
        TagItemImpl tags = (TagItemImpl) tags0;
        while (tags != null) {
            if (tags.tag == tag)
                return tags.data;
            tags = next(tags);
        }
        return defaultValue;
    }

    public static int tagInt(TagItem tags0, int tag, int defaultValue) {
        TagItemImpl tags = (TagItemImpl) tags0;
        while (tags != null) {
            if (tags.tag == tag)
                return tags.lint;
            tags = next(tags);
        }
        return defaultValue;
    }

    public static Integer tagInteger(TagItem tags0, int tag) {
        TagItemImpl tags = (TagItemImpl) tags0;
        while (tags != null) {
            if (tags.tag == tag)
                return tags.lint;
            tags = next(tags);
        }
        return null;
    }

    public static String tagString(TagItem tags0, int tag, String defaultValue) {
        TagItemImpl tags = (TagItemImpl) tags0;
        while (tags != null) {
            if (tags.tag == tag) {
                @SuppressWarnings("unchecked")
                IRef<String> textRef = (IRef<String>) tags.addr;
                return textRef.get();
            }
            tags = next(tags);
        }
        return defaultValue;
    }

    public static Object tagObject(TagItem tags0, int tag, String defaultValue) {
        TagItemImpl tags = (TagItemImpl) tags0;
        while (tags != null) {
            if (tags.tag == tag) {
                return tags.addr;
            }
            tags = next(tags);
        }
        return defaultValue;
    }

    public void begin() {

    }

    public void close() {
        System.exit(0);
    }
}
