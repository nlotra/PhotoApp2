package com.example.photoapp2.app;

/**
 * Created by Natasha Lotra on 2014/06/25.
 */
public class Photo
{
    private String id, owner, secret, server, farm, title, url;

    public Photo(String url)
    {
        this.url = url;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public void setOwner(String owner)
    {
        this.owner = owner;
    }

    public void setSecret(String secret)
    {
        this.secret = secret;
    }

    public void setServer(String server)
    {
        this.server = server;
    }

    public void setFarm(String farm)
    {
        this.farm = farm;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getId()
    {
        return id;
    }

    public String getOwner()
    {
        return owner;
    }

    public String getSecret()
    {
        return secret;
    }

    public String getServer()
    {
        return server;
    }

    public String getFarm()
    {
        return farm;
    }

    public String getTitle()
    {
        return title;
    }

    public String getUrl()
    {
        return url;
    }
}
