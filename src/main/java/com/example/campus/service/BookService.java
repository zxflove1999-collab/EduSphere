package com.example.campus.service;

import com.example.campus.common.BusinessException;
import com.example.campus.common.PageResult;
import com.example.campus.entity.Book;
import com.example.campus.entity.BookTag;
import com.example.campus.entity.Tag;
import com.example.campus.mapper.BookMapper;
import com.example.campus.mapper.BookTagMapper;
import com.example.campus.mapper.TagMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 书籍管理服务
 */
@Service
@RequiredArgsConstructor
public class BookService {
    private final BookMapper bookMapper;
    private final TagMapper tagMapper;
    private final BookTagMapper bookTagMapper;

    /**
     * 上传书籍
     */
    @Transactional
    public void uploadBook(Book book, Long uploaderId, List<String> tags) {
        // 检查ISBN是否已存在
        Book existing = bookMapper.selectByIsbn(book.getIsbn());
        if (existing != null) {
            throw new BusinessException("ISBN已存在");
        }
        
        book.setUploaderId(uploaderId);
        book.setAvailableCopies(book.getTotalCopies());
        bookMapper.insert(book);
        
        // 处理标签
        if (tags != null && !tags.isEmpty()) {
            for (String tagName : tags) {
                Tag tag = tagMapper.selectByTagName(tagName);
                if (tag == null) {
                    // 创建新标签
                    Tag newTag = new Tag();
                    newTag.setTagName(tagName);
                    tagMapper.insert(newTag);
                    tag = newTag;
                }
                
                // 关联标签
                BookTag bookTag = new BookTag();
                bookTag.setBookId(book.getBookId());
                bookTag.setTagId(tag.getTagId());
                bookTagMapper.insert(bookTag);
            }
        }
    }

    /**
     * 图书列表查询
     */
    public PageResult<Book> listBooks(Integer page, Integer pageSize, String title, String author,
                                     String tagName, Boolean isAdminView) {
        PageHelper.startPage(page != null ? page : 1, pageSize != null ? pageSize : 10);
        List<Book> books = bookMapper.selectByCondition(title, author, tagName, isAdminView);
        PageInfo<Book> pageInfo = new PageInfo<>(books);
        return new PageResult<>(pageInfo.getTotal(), books);
    }

    /**
     * 添加书籍标签
     */
    @Transactional
    public void addBookTag(Long bookId, String tagName) {
        // 检查书籍是否存在
        Book book = bookMapper.selectById(bookId);
        if (book == null) {
            throw new BusinessException("书籍不存在");
        }
        if (!StringUtils.hasText(tagName)) {
            throw new BusinessException("标签名称不能为空");
        }
        String trimmedTagName = tagName.trim();
        
        // 检查标签是否存在
        Tag tag = tagMapper.selectByTagName(trimmedTagName);
        if (tag == null) {
            // 创建新标签
            tag = new Tag();
            tag.setTagName(trimmedTagName);
            tagMapper.insert(tag);
        }
        
        // 检查关联是否已存在
        List<BookTag> existing = bookTagMapper.selectByBookId(bookId);
        final Integer finalTagId = tag.getTagId();
        boolean exists = existing.stream().anyMatch(bt -> bt.getTagId().equals(finalTagId));
        if (exists) {
            throw new BusinessException("该标签已存在");
        }
        
        // 创建关联
        BookTag bookTag = new BookTag();
        bookTag.setBookId(bookId);
        bookTag.setTagId(tag.getTagId());
        bookTagMapper.insert(bookTag);
    }
}

