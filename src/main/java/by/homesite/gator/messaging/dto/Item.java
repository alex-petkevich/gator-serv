package by.homesite.gator.messaging.dto;

import java.io.Serializable;

public class Item implements Serializable
{
    private Long id;
    private String siteName;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getSiteName()
    {
        return siteName;
    }

    public void setSiteName(String siteName)
    {
        this.siteName = siteName;
    }
}
