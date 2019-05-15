package io.netty.buffer;

import io.netty.util.internal.StringUtil;




















final class PoolChunkList<T>
{
  private final PoolArena<T> arena;
  private final PoolChunkList<T> nextList;
  PoolChunkList<T> prevList;
  private final int minUsage;
  private final int maxUsage;
  private PoolChunk<T> head;
  
  PoolChunkList(PoolArena<T> arena, PoolChunkList<T> nextList, int minUsage, int maxUsage)
  {
    this.arena = arena;
    this.nextList = nextList;
    this.minUsage = minUsage;
    this.maxUsage = maxUsage;
  }
  
  boolean allocate(PooledByteBuf<T> buf, int reqCapacity, int normCapacity) {
    if (head == null) {
      return false;
    }
    
    PoolChunk<T> cur = head;
    for (;;) { long handle = cur.allocate(normCapacity);
      if (handle < 0L) {
        cur = next;
        if (cur == null) {
          return false;
        }
      } else {
        cur.initBuf(buf, handle, reqCapacity);
        if (cur.usage() >= maxUsage) {
          remove(cur);
          nextList.add(cur);
        }
        return true;
      }
    }
  }
  
  void free(PoolChunk<T> chunk, long handle) {
    chunk.free(handle);
    if (chunk.usage() < minUsage) {
      remove(chunk);
      if (prevList == null) {
        assert (chunk.usage() == 0);
        arena.destroyChunk(chunk);
      } else {
        prevList.add(chunk);
      }
    }
  }
  
  void add(PoolChunk<T> chunk) {
    if (chunk.usage() >= maxUsage) {
      nextList.add(chunk);
      return;
    }
    
    parent = this;
    if (head == null) {
      head = chunk;
      prev = null;
      next = null;
    } else {
      prev = null;
      next = head;
      head.prev = chunk;
      head = chunk;
    }
  }
  
  private void remove(PoolChunk<T> cur) {
    if (cur == head) {
      head = next;
      if (head != null) {
        head.prev = null;
      }
    } else {
      PoolChunk<T> next = next;
      prev.next = next;
      if (next != null) {
        prev = prev;
      }
    }
  }
  
  public String toString()
  {
    if (head == null) {
      return "none";
    }
    
    StringBuilder buf = new StringBuilder();
    PoolChunk<T> cur = head;
    for (;;) { buf.append(cur);
      cur = next;
      if (cur == null) {
        break;
      }
      buf.append(StringUtil.NEWLINE);
    }
    
    return buf.toString();
  }
}
